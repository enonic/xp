package com.enonic.xp.module;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationKey;

import static org.junit.Assert.*;

public class ApplicationKeyTest
{
    @Test
    public void testCreateModule()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "mymodule" );

        assertEquals( ApplicationKey.from( "mymodule" ).toString(), applicationKey.toString() );
        assertEquals( ApplicationKey.from( "mymodule" ).hashCode(), -67255528 );
    }

    @Test
    public void testParseModuleVersion()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "mymodule" );

        assertEquals( ApplicationKey.from( "mymodule" ), applicationKey );
    }

    @Test
    public void fromBundle()
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( "mymodule" );
        ApplicationKey applicationKey = ApplicationKey.from( bundle );

        assertEquals( ApplicationKey.from( "mymodule" ).toString(), applicationKey.toString() );
    }

}
