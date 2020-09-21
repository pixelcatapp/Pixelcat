/*
 * Copyright (C) 2020 Conny Duck
 *
 * This file is part of Pixelcat.
 *
 * Pixelcat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pixelcat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package at.connyduck.pixelcat.network.calladapter

import com.squareup.moshi.Types
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import retrofit2.Call
import retrofit2.Retrofit

class NetworkResponseAdapterFactoryTest {

    private val retrofit = Retrofit.Builder().baseUrl("http://example.com").build()

    @Test
    fun `should return a NetworkResponseCallAdapter when the type is supported`() {
        val networkResponseType =
            Types.newParameterizedType(NetworkResponse::class.java, TestResponseClass::class.java)
        val callType = Types.newParameterizedType(Call::class.java, networkResponseType)

        val adapter = NetworkResponseAdapterFactory().get(callType, arrayOf(), retrofit)

        assertEquals(TestResponseClass::class.java, adapter?.responseType())
    }

    @Test
    fun `should return null if the type is not supported`() {

        val adapter = NetworkResponseAdapterFactory().get(TestResponseClass::class.java, arrayOf(), retrofit)

        assertNull(adapter)
    }
}
