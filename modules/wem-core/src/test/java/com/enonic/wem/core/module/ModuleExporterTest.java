package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class ModuleExporterTest
{
    private Path tempDir;

    @Before
    public void createTempDir()
        throws IOException
    {
        tempDir = Files.createTempDirectory( "wemtest" );
    }

    @After
    public void deleteTempDir()
        throws Exception
    {
        FileUtils.deleteDirectory( tempDir.toFile() );
    }

    @Test
    public void testExportModuleToZip()
        throws Exception
    {
        final Module module = createModule();

        final Path exportedModuleZip = new ModuleExporter().exportToZip( module, tempDir );
        System.out.println( "Module exported to " + exportedModuleZip );

        assertNotNull( exportedModuleZip );
        assertTrue( Files.exists( exportedModuleZip ) && Files.isRegularFile( exportedModuleZip ) );
    }

    @Test
    public void testExportModuleToDirectory()
        throws Exception
    {
        final Module module = createModule();

        final Path exportedModuleDir = new ModuleExporter().exportToDirectory( module, tempDir );
        System.out.println( "Module exported to " + exportedModuleDir );

        assertNotNull( exportedModuleDir );
        assertTrue( Files.exists( exportedModuleDir ) && Files.isDirectory( exportedModuleDir ) );
    }

    private Module createModule()
    {
        final Form config = Form.newForm().
            addFormItem( Input.newInput().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        return new ModuleBuilder().
            moduleKey( ModuleKey.from( "testmodule-1.0.0" ) ).
            displayName( "module display name" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            config( config ).
            build();
    }
}
