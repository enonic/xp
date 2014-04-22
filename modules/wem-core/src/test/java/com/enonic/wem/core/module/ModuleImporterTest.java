package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;

import static org.junit.Assert.*;

public class ModuleImporterTest
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
    public void testImportModuleFromDirectory()
        throws Exception
    {
        final Module module = createModule();
        final ModuleExporter exporter = new ModuleExporter();

        exporter.pathResolver = Mockito.mock( ModuleResourcePathResolver.class );

        final Path exportedModuleDir = exporter.exportToDirectory( module, tempDir );
        Mockito.when( exporter.pathResolver.resolveModulePath( module.getKey() ) ).thenReturn( exportedModuleDir );

        final Module importedModule = exporter.importFromDirectory( exportedModuleDir ).build();

        assertNotNull( importedModule );
        assertEquals( "testmodule-1.0.0", importedModule.getKey().toString() );
        assertEquals( "testmodule", importedModule.getName().toString() );
        assertEquals( "1.0.0", importedModule.getVersion().toString() );
        assertEquals( "module display name", importedModule.getDisplayName() );
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
