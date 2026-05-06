package com.tomosensei.feature.gateconfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomosensei.core.data.db.dao.TriggerConfigDao
import com.tomosensei.core.data.db.entity.TriggerConfigEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TriggerRowUi(
    val type: String,
    val label: String,
    val intensity: Int,
    val enabled: Boolean,
    val cooldownSeconds: Int,
    val dailyCap: Int,
)

@HiltViewModel
class GateConfigViewModel @Inject constructor(
    private val triggerDao: TriggerConfigDao,
) : ViewModel() {

    val rows: StateFlow<List<TriggerRowUi>> = triggerDao.observeAll()
        .map { stored -> mergeWithDefaults(stored) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = mergeWithDefaults(emptyList()),
        )

    fun setIntensity(type: String, intensity: Int) {
        viewModelScope.launch {
            val existing = triggerDao.get(type)
            val row = (existing ?: defaults.first { it.triggerType == type })
                .copy(intensity = intensity, enabled = intensity > 0)
            triggerDao.upsert(row)
        }
    }

    private fun mergeWithDefaults(stored: List<TriggerConfigEntity>): List<TriggerRowUi> {
        val byType = stored.associateBy { it.triggerType }
        return defaults.map { d ->
            val s = byType[d.triggerType] ?: d
            TriggerRowUi(
                type = s.triggerType,
                label = labels[s.triggerType] ?: s.triggerType,
                intensity = s.intensity,
                enabled = s.enabled,
                cooldownSeconds = s.cooldownSeconds,
                dailyCap = s.dailyCap,
            )
        }
    }

    companion object {
        private val labels = mapOf(
            "unlock" to "Unlock HP",
            "app_launch" to "Buka app distraksi",
            "app_switch" to "App switch",
            "idle" to "Idle (20 menit)",
        )
        private val defaults = listOf(
            TriggerConfigEntity("unlock", true, 3, 60, 30, "every"),
            TriggerConfigEntity("app_launch", true, 3, 30, 50, "every"),
            TriggerConfigEntity("app_switch", false, 3, 60, 20, "every_nth:5"),
            TriggerConfigEntity("idle", true, 2, 120, 10, "every"),
        )
    }
}
