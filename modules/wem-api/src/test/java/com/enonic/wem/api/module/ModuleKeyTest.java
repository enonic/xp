package com.enonic.wem.api.module;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ModuleKeyTest
{

    @Test
    public void testCreateModuleVersion()
    {
        final ModuleKey moduleKey = ModuleKey.from( ModuleName.from( "mymodule" ), ModuleVersion.from( 3, 2, 1 ) );

        assertEquals( ModuleName.from( "mymodule" ), moduleKey.getName() );
        assertEquals( ModuleVersion.from( "3.2.1" ), moduleKey.getVersion() );
    }

    @Test
    public void testParseModuleVersion()
    {
        final ModuleKey moduleKey = ModuleKey.from( "mymodule-3.2.1" );

        assertEquals( ModuleKey.from( ModuleName.from( "mymodule" ), ModuleVersion.from( 3, 2, 1 ) ), moduleKey );
    }

    @Test
    public void testModuleVersionToStringParse()
    {
        final ModuleKey moduleKey = ModuleKey.from( ModuleName.from( "mymodule" ), ModuleVersion.from( 3, 2, 1 ) );
        final String moduleVersionString = moduleKey.toString();
        final ModuleKey parsedModuleVerison = ModuleKey.from( moduleVersionString );

        assertEquals( "mymodule-3.2.1", moduleVersionString );
        assertEquals( moduleKey, parsedModuleVerison );
        assertEquals( moduleKey.toString(), parsedModuleVerison.toString() );
    }
}
