package com.enonic.xp.module;

import org.junit.Test;

import com.enonic.xp.module.ModuleKey;

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
