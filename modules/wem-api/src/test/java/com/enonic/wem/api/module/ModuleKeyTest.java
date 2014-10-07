package com.enonic.wem.api.module;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ModuleKeyTest
{
    @Test
    public void testCreateModule()
    {
        final ModuleKey moduleKey = ModuleKey.from( "mymodule" );

        assertEquals( ModuleKey.from( "mymodule" ).toString(), moduleKey.toString() );
    }

    @Test
    public void testParseModuleVersion()
    {
        final ModuleKey moduleKey = ModuleKey.from( "mymodule" );

        assertEquals( ModuleKey.from( "mymodule" ), moduleKey );
    }

}
