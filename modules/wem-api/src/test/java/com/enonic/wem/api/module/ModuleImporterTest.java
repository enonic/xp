package com.enonic.wem.api.module;

import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

public class ModuleImporterTest
{
    @Test
    @Ignore
    public void testImportModule()
        throws Exception
    {
        final String importFile = "~/tmp/module/mymodule-1.0.0".replace( "~", System.getProperty( "user.home" ) );
        final Module module = new ModuleImporter().importModuleFromDirectory( Paths.get( importFile ) );

        final byte[] data = module.getModuleDirectoryEntry().getEntry( "public/css/mystyles.css" ).getResource().getByteSource().read();
        System.out.println( new String( data ) );
        System.out.println( module.getModuleDirectoryEntry().asTreeString() );
    }
}
