package com.enonic.xp.module;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import static org.junit.Assert.*;

public class ModuleKeyTest
{
    @Test
    public void testCreateModule()
    {
        final ModuleKey moduleKey = ModuleKey.from( "mymodule" );

        assertEquals( ModuleKey.from( "mymodule" ).toString(), moduleKey.toString() );
        assertEquals( ModuleKey.from( "mymodule" ).hashCode(), -67255528 );
    }

    @Test
    public void testParseModuleVersion()
    {
        final ModuleKey moduleKey = ModuleKey.from( "mymodule" );

        assertEquals( ModuleKey.from( "mymodule" ), moduleKey );
    }

    @Test
    public void fromBundle()
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( "mymodule" );
        ModuleKey moduleKey = ModuleKey.from( bundle );

        assertEquals( ModuleKey.from( "mymodule" ).toString(), moduleKey.toString() );
    }

}
