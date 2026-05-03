package com.tomosensei.feature.drill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomosensei.core.common.Clock
import com.tomosensei.core.data.db.entity.CardEntity
import com.tomosensei.core.data.repository.CardRepository
import com.tomosensei.core.data.repository.ProgressRepository
import com.tomosensei.core.data.repository.UserStatsRepository
import com.tomosensei.core.srs.FsrsRating
import com.tomosensei.core.srs.FsrsScheduler
import com.tomosensei.feature.drill.model.DrillCardUi
import com.tomosensei.feature.drill.model.DrillExampleUi
import com.tomosensei.feature.drill.model.DrillUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray

@HiltViewModel
class DrillViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val progressRepository: ProgressRepository,
    private val statsRepository: UserStatsRepository,
    private val scheduler: FsrsScheduler,
    private val clock: Clock,
) : ViewModel() {

    private val _state = MutableStateFlow<DrillUiState>(DrillUiState.Loading)
    val state: StateFlow<DrillUiState> = _state.asStateFlow()

    private var queue: ArrayDeque<CardEntity> = ArrayDeque()

    init {
        viewModelScope.launch { refillQueue() }
    }

    fun onTapFlip() {
        val current = _state.value as? DrillUiState.Ready ?: return
        _state.value = current.copy(flipped = !current.flipped)
    }

    fun onAnswer(knew: Boolean) {
        val current = _state.value as? DrillUiState.Ready ?: return
        val now = clock.nowMillis()
        viewModelScope.launch {
            val rating = if (knew) FsrsRating.GOOD else FsrsRating.AGAIN
            val fsrs = progressRepository.fsrsCard(current.current.id, now)
            val updated = scheduler.review(fsrs, rating, now)
            progressRepository.saveFsrsCard(updated)
            advanceQueue()
        }
    }

    private suspend fun advanceQueue() {
        if (queue.isNotEmpty()) queue.removeFirst()
        if (queue.isEmpty()) refillQueue() else publishHead()
    }

    private suspend fun refillQueue() {
        val all = cardRepository.observeByLevel("N5").first()
        if (all.isEmpty()) {
            _state.value = DrillUiState.Empty
            return
        }
        val now = clock.nowMillis()
        // Order cards by FSRS due time; new cards (no row) sort to front.
        val ordered = all
            .map { card -> card to (progressRepository.get(card.id)?.due ?: 0L) }
            .sortedBy { it.second }
            .map { it.first }
        queue = ArrayDeque(ordered)
        publishHead()
    }

    private suspend fun publishHead() {
        val head = queue.firstOrNull()
        if (head == null) {
            _state.value = DrillUiState.Empty
            return
        }
        val stats = statsRepository.get()
        _state.value = DrillUiState.Ready(
            current = head.toUi(),
            flipped = false,
            reviewedToday = stats?.totalReviews ?: 0,
            streakDays = stats?.streak ?: 0,
        )
    }

    private fun CardEntity.toUi(): DrillCardUi {
        val examples = parseExamples(this.examples)
        return DrillCardUi(
            id = id,
            front = front,
            reading = reading,
            meaning = meaning,
            type = type,
            examples = examples,
        )
    }

    private fun parseExamples(json: String): List<DrillExampleUi> {
        if (json.isBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                DrillExampleUi(
                    jp = obj.optString("jp"),
                    reading = obj.optString("reading"),
                    translation = obj.optString("id"),
                )
            }
        }.getOrDefault(emptyList())
    }
}
