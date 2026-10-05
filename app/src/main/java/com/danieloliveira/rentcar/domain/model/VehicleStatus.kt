package com.danieloliveira.rentcar.domain.model

enum class VehicleStatus(val label: String) {
    DISPONIVEL("Disponível"),
    ALUGADO("Alugado"),
    MANUTENCAO("Manutenção");

    companion object {
        fun labelOf(value: String): String =
            entries.firstOrNull { it.name == value }?.label ?: value
    }
}
