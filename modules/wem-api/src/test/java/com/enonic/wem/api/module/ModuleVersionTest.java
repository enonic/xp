package com.enonic.wem.api.module;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ModuleVersionTest
{

    @Test
    public void testCreateModuleVersion()
    {
        final ModuleVersion moduleVersion = new ModuleVersion( ModuleName.from( "mymodule" ), Version.from( 3, 2, 1 ) );

        assertEquals( ModuleName.from( "mymodule" ), moduleVersion.getName() );
        assertEquals( Version.from( "3.2.1" ), moduleVersion.getVersion() );
    }

    @Test
    public void testParseModuleVersion()
    {
        final ModuleVersion moduleVersion = ModuleVersion.parse( "mymodule-3.2.1" );

        assertEquals( new ModuleVersion( ModuleName.from( "mymodule" ), Version.from( 3, 2, 1 ) ), moduleVersion );
    }

    @Test
    public void testModuleVersionToStringParse()
    {
        final ModuleVersion moduleVersion = new ModuleVersion( ModuleName.from( "mymodule" ), Version.from( 3, 2, 1 ) );
        final String moduleVersionString = moduleVersion.toString();
        final ModuleVersion parsedModuleVerison = ModuleVersion.parse( moduleVersionString );

        assertEquals( "mymodule-3.2.1", moduleVersionString );
        assertEquals( moduleVersion, parsedModuleVerison );
        assertEquals( moduleVersion.toString(), parsedModuleVerison.toString() );
    }
}
