package com.danieloliveira.rentcar.domain.validator

object VehicleValidator {
    private val oldPlateRegex = Regex("^[A-Z]{3}-[0-9]{4}$")
    private val mercosulPlateRegex = Regex("^[A-Z]{3}[0-9][A-Z][0-9]{2}$")

    fun normalizePlate(value: String): String = value.trim().uppercase()

    fun validate(
        brand: String,
        model: String,
        plate: String,
        year: String,
        dailyRate: String
    ): String? {
        if (brand.isBlank() || model.isBlank() || plate.isBlank() || year.isBlank() || dailyRate.isBlank()) {
            return "Preencha todos os campos obrigatórios."
        }

        val normalizedPlate = normalizePlate(plate)
        if (!oldPlateRegex.matches(normalizedPlate) && !mercosulPlateRegex.matches(normalizedPlate)) {
            return "Informe uma placa válida: AAA-1234 ou AAA1A23."
        }

        val yearNumber = year.toIntOrNull()
        if (yearNumber == null || yearNumber <= 0) {
            return "Informe um ano válido."
        }

        val dailyRateNumber = dailyRate.replace(',', '.').toDoubleOrNull()
        if (dailyRateNumber == null || dailyRateNumber <= 0.0) {
            return "O valor da diária deve ser numérico e positivo."
        }

        return null
    }
}
