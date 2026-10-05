package com.danieloliveira.rentcar

import com.danieloliveira.rentcar.domain.validator.VehicleValidator
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VehicleValidatorTest {
    @Test
    fun placaAntigaValida() {
        val result = VehicleValidator.validate(
            brand = "Toyota",
            model = "Corolla",
            plate = "ABC-1234",
            year = "2024",
            dailyRate = "180"
        )
        assertNull(result)
    }

    @Test
    fun placaMercosulValida() {
        val result = VehicleValidator.validate(
            brand = "Honda",
            model = "Civic",
            plate = "ABC1D23",
            year = "2024",
            dailyRate = "200,50"
        )
        assertNull(result)
    }

    @Test
    fun diariaNegativaEInvalida() {
        val result = VehicleValidator.validate(
            brand = "Honda",
            model = "Civic",
            plate = "ABC1D23",
            year = "2024",
            dailyRate = "-10"
        )
        assertTrue(result?.contains("positiv", ignoreCase = true) == true)
    }
}
