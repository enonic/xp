package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.ByteStreams;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

import static com.enonic.wem.api.module.ModuleFileEntry.directoryBuilder;
import static com.enonic.wem.api.module.ModuleFileEntry.newFileEntry;
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
    {
        try
        {
            FileUtils.deleteDirectory( tempDir.toFile() );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testExportModuleToZip()
        throws Exception
    {
        final Module module = createModule();

        final Path exportedModuleZip = new ModuleExporter().exportModuleToZip( module, tempDir );
        System.out.println( "Module exported to " + exportedModuleZip );

        assertNotNull( exportedModuleZip );
        assertTrue( Files.exists( exportedModuleZip ) && Files.isRegularFile( exportedModuleZip ) );
    }

    @Test
    public void testExportModuleToDirectory()
        throws Exception
    {
        final Module module = createModule();

        final Path exportedModuleDir = new ModuleExporter().exportModuleToDirectory( module, tempDir );
        System.out.println( "Module exported to " + exportedModuleDir );

        assertNotNull( exportedModuleDir );
        assertTrue( Files.exists( exportedModuleDir ) && Files.isDirectory( exportedModuleDir ) );
    }

    private Module createModule()
    {
        final ModuleFileEntry publicDir = directoryBuilder( "public" ).
            addEntry( newFileEntry( "file1.txt", ByteStreams.asByteSource( "some data".getBytes() ) ) ).
            build();
        final ModuleFileEntry templatesDir = directoryBuilder( "templates" ).
            addEntry( newFileEntry( "template1.txt", ByteStreams.asByteSource( "some more data".getBytes() ) ) ).
            build();

        final Form config = Form.newForm().
            addFormItem( Input.newInput().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        final ContentTypeNames requiredCtypes = ContentTypeNames.from( "ctype1", "ctype2", "ctype3" );
        final ModuleKeys requiredModules = ModuleKeys.from( ModuleKey.from( "modA-1.0.0" ), ModuleKey.from( "modB-1.0.0" ) );

        final Module module = Module.newModule().
            moduleKey( ModuleKey.from( "testmodule-1.0.0" ) ).
            displayName( "module display name" ).
            info( "module-info" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            minSystemVersion( ModuleVersion.from( 5, 0, 0 ) ).
            maxSystemVersion( ModuleVersion.from( 6, 0, 0 ) ).
            addModuleDependency( ModuleKey.from( "modulefoo-1.0.0" ) ).
            addContentTypeDependency( ContentTypeName.from( "article" ) ).
            addModuleDependencies( requiredModules ).
            addContentTypeDependencies( requiredCtypes ).
            config( config ).
            addFileEntry( publicDir ).
            addFileEntry( templatesDir ).
            addFileEntry( directoryBuilder( "emptydir" ).build() ).
            build();
        return module;
    }
}
