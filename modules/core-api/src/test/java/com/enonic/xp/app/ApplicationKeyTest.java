package com.enonic.xp.app;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import static org.junit.Assert.*;

public class ApplicationKeyTest
{
    @Test
    public void testCreate()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        assertEquals( ApplicationKey.from( "myapplication" ).toString(), applicationKey.toString() );
    }

    @Test
    public void testEquals()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        assertEquals( ApplicationKey.from( "myapplication" ), applicationKey );
        assertEquals( ApplicationKey.from( "myapplication" ).hashCode(), applicationKey.hashCode() );
    }

    @Test
    public void testFromApplicationKey()
    {
        assertEquals( "myapplication", ApplicationKey.from( "myapplication" ).getName() );
    }

    @Test
    public void testParseApplicationVersion()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        assertEquals( ApplicationKey.from( "myapplication" ), applicationKey );
    }

    @Test
    public void fromBundle()
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( "myapplication" );
        ApplicationKey applicationKey = ApplicationKey.from( bundle );

        assertEquals( ApplicationKey.from( "myapplication" ).toString(), applicationKey.toString() );
    }
}