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
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

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
        final Path exportedModuleDir = new ModuleExporter().exportToDirectory( module, tempDir );

        final Module importedModule = new ModuleExporter().importFromDirectory( exportedModuleDir ).build();

        assertNotNull( importedModule );
        assertEquals( "testmodule-1.0.0", importedModule.getModuleKey().toString() );
        assertEquals( "testmodule", importedModule.getName().toString() );
        assertEquals( "1.0.0", importedModule.getVersion().toString() );
        assertEquals( "module display name", importedModule.getDisplayName() );
    }

    @Test
    public void testImportModuleFromZip()
        throws Exception
    {
        final Module module = createModule();
        final Path exportedModuleZip = new ModuleExporter().exportToZip( module, tempDir );

        final Module importedModule = new ModuleExporter().importFromZip( exportedModuleZip ).build();

        assertEquals( "testmodule-1.0.0", importedModule.getModuleKey().toString() );
        assertEquals( "testmodule", importedModule.getName().toString() );
        assertEquals( "1.0.0", importedModule.getVersion().toString() );
        assertEquals( "module display name", importedModule.getDisplayName() );
    }

    private Module createModule()
    {
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
            build();
        return module;
    }
}
