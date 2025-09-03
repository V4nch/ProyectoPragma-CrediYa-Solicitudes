package co.com.pragma.powerup.model.utils;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;


class ConstantsTest {

    @Test
    void testConstantsClassLoads() {
        assertNotNull(Constants.STATUS_PENDING_REVIEW);
        assertEquals("application/json", Constants.CONTENT_TYPE);
    }
    @Test
     void testPrivateConstructor() throws Exception {
        var constructor = Constants.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertTrue(exception.getCause() instanceof IllegalStateException);
    }
}