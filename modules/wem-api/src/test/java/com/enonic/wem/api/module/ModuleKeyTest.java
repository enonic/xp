package com.enonic.wem.api.module;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ModuleKeyTest
{
    @Test
    public void testCreateModuleVersion()
    {
        final ModuleKey moduleKey = ModuleKey.from( ModuleName.from( "mymodule" ), ModuleVersion.from( "3.2.1" ) );

        assertEquals( ModuleName.from( "mymodule" ), moduleKey.getName() );
        assertEquals( ModuleVersion.from( "3.2.1" ), moduleKey.getVersion() );
    }

    @Test
    public void testParseModuleVersion()
    {
        final ModuleKey moduleKey = ModuleKey.from( "mymodule-3.2.1" );

        assertEquals( ModuleKey.from( ModuleName.from( "mymodule" ), ModuleVersion.from( "3.2.1" ) ), moduleKey );
    }

    @Test
    public void testParseModuleNameWithDash()
    {
        final ModuleKey moduleKey = ModuleKey.from( "my-module-3.2.1" );

        assertEquals( ModuleKey.from( ModuleName.from( "my-module" ), ModuleVersion.from( "3.2.1" ) ), moduleKey );
    }

    @Test
    public void testParseSystemModuleWithoutVersion()
    {
        final ModuleKey systemModuleKey = ModuleKey.from( "system" );
        final ModuleKey systemModuleKeyWithVersion = ModuleKey.from( "system-0.0.0" );

        assertEquals( ModuleKey.from( ModuleName.from( "system" ), ModuleVersion.from( "0.0.0" ) ), systemModuleKey );
        assertEquals( ModuleKey.from( ModuleName.from( "system" ), ModuleVersion.from( "0.0.0" ) ), systemModuleKeyWithVersion );
        assertEquals( systemModuleKey, systemModuleKeyWithVersion );
        assertEquals( ModuleKey.SYSTEM, systemModuleKey );
    }

    @Test
    public void testModuleVersionToStringParse()
    {
        final ModuleKey moduleKey = ModuleKey.from( ModuleName.from( "mymodule" ), ModuleVersion.from( "3.2.1" ) );
        final String moduleVersionString = moduleKey.toString();
        final ModuleKey parsedModuleVerison = ModuleKey.from( moduleVersionString );

        assertEquals( "mymodule-3.2.1", moduleVersionString );
        assertEquals( moduleKey, parsedModuleVerison );
        assertEquals( moduleKey.toString(), parsedModuleVerison.toString() );
    }
}
