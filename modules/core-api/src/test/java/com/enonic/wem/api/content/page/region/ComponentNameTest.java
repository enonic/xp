package com.enonic.wem.api.content.page.region;

import org.junit.Test;

import static org.junit.Assert.*;

public class ComponentNameTest
{
    @Test(expected = NullPointerException.class)
    public void constructor_throws_NullPointerException_when_given_value_is_null()
    {
        new ComponentName( null );
    }

    @Test
    public void to_string()
    {
        assertEquals( "hello", new ComponentName( "hello" ).toString() );
    }
}