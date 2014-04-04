package com.enonic.wem.api.module;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

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
    public void testParseModuleNameWithDash()
    {
        final ModuleKey moduleKey = ModuleKey.from( "my-module-3.2.1" );

        assertEquals( ModuleKey.from( ModuleName.from( "my-module" ), ModuleVersion.from( 3, 2, 1 ) ), moduleKey );
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

    @Test
    public void testSystemModule()
    {
        final ModuleKey system = ModuleKey.SYSTEM;

        assertNotNull( system );
        assertEquals( "system", system.getName().toString() );
        assertEquals( "0.0.0", system.getVersion().toString() );
    }

    @Test
    public void testIsSystemModule()
    {
        assertTrue( ModuleKey.SYSTEM.isSystem() );
        assertFalse( ModuleKey.from( "system-1.1.1" ).isSystem() );
        assertFalse( ModuleKey.from( "test-1.1.1" ).isSystem() );
    }
}
