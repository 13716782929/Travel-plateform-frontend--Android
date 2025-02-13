package iss.nus.edu.sg.mygo

import iss.nus.edu.sg.mygo.models.FlightBookingRequest
import org.junit.Assert.assertEquals
import org.junit.Test

class FlightBookingUnitTest {
    @Test
    fun `test flight booking request creation`() {
        val request = FlightBookingRequest(
            userId = 123,
            selectedSeats = "A1,A2",
            id = 1,
            type = "Business",
            totalPrice = 1000.0
        )

        assertEquals(123, request.userId)
        assertEquals("A1,A2", request.selectedSeats)
        assertEquals(1, request.id)
        assertEquals("Business", request.type)
        assertEquals(1000.0, request.totalPrice, 0.001)
    }
}
// test
