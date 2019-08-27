package com.enonic.xp.region;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ComponentNameTest
{
    @Test
    public void constructor_throws_NullPointerException_when_given_value_is_null()
    {
        assertThrows(NullPointerException.class, () -> new ComponentName( null ));
    }

    @Test
    public void to_string()
    {
        assertEquals( "hello", new ComponentName( "hello" ).toString() );
    }
}
