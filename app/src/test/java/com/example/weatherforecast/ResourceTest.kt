package com.example.weatherforecast

import com.example.weatherforecast.utils.Resource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResourceTest {

    @Test
    fun success_holdsData() {
        val resource = Resource.Success("test-data")

        assertTrue(resource is Resource.Success)
        assertEquals("test-data", resource.data)
        assertNull(resource.msg)
    }

    @Test
    fun success_withNullData() {
        val resource = Resource.Success<String>(null)

        assertNull(resource.data)
    }

    @Test
    fun success_isStaleFlag() {
        val resource = Resource.Success("data", isStale = true)

        assertTrue(resource.isStale)
    }

    @Test
    fun error_holdsMessage() {
        val resource = Resource.Error<String>(msg = "error-msg")

        assertTrue(resource is Resource.Error)
        assertEquals("error-msg", resource.msg)
        assertNull(resource.data)
    }

    @Test
    fun error_holdsDataAndMessage() {
        val resource = Resource.Error("partial-data", msg = "error-with-data")

        assertTrue(resource is Resource.Error)
        assertEquals("partial-data", resource.data)
        assertEquals("error-with-data", resource.msg)
    }

    @Test
    fun loading_hasNoData() {
        val resource = Resource.Loading<String>()

        assertTrue(resource is Resource.Loading)
        assertNull(resource.data)
        assertNull(resource.msg)
    }

    @Test
    fun internet_hasNoData() {
        val resource = Resource.Internet<String>()

        assertTrue(resource is Resource.Internet)
        assertNull(resource.data)
        assertNull(resource.msg)
    }

    @Test
    fun whenExpression_matchesCorrectType() {
        val success: Resource<String> = Resource.Success("hello")
        val error: Resource<String> = Resource.Error(msg = "fail")
        val loading: Resource<String> = Resource.Loading()
        val internet: Resource<String> = Resource.Internet()

        assertTrue(whenResourceType(success) == "success")
        assertTrue(whenResourceType(error) == "error")
        assertTrue(whenResourceType(loading) == "loading")
        assertTrue(whenResourceType(internet) == "internet")
    }

    private fun whenResourceType(resource: Resource<String>): String {
        return when (resource) {
            is Resource.Success -> "success"
            is Resource.Error -> "error"
            is Resource.Loading -> "loading"
            is Resource.Internet -> "internet"
        }
    }

    @Test
    fun success_isNotStaleByDefault() {
        val resource = Resource.Success("data")

        assertEquals(false, resource.isStale)
    }
}
