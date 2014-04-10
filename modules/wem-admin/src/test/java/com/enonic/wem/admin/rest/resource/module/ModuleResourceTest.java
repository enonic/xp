package com.enonic.wem.admin.rest.resource.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

import static com.enonic.wem.api.module.ModuleFileEntry.newFileEntry;
import static com.enonic.wem.api.module.ModuleFileEntry.newModuleDirectory;

public class ModuleResourceTest
    extends AbstractResourceTest
{
    private ModuleService moduleService;

    private Path tempDir;

    @Before
    public void setup()
        throws IOException
    {
        mockCurrentContextHttpRequest();
        tempDir = Files.createTempDirectory( "wemtest" );
    }

    @After
    public void after()
    {
        try
        {
            if ( tempDir != null )
            {
                FileUtils.deleteDirectory( tempDir.toFile() );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    @Test
    public void get_module_by_key()
        throws Exception
    {
        final Module module = createModule();
        Mockito.when( this.moduleService.getModule( Mockito.isA( ModuleKey.class ) ) ).thenReturn( module );

        String response = resource().
            path( "module" ).
            queryParam( "moduleKey", "testmodule-1.0.0" ).
            get( String.class );
        assertJson( "get_module_by_key_success.json", response );
    }

    private Module createModule()
    {
        final ModuleFileEntry publicDir = newModuleDirectory( "public" ).
            addEntry( newFileEntry( "file1.txt", ByteSource.wrap( "some data".getBytes() ) ) ).
            build();
        final ModuleFileEntry templatesDir = newModuleDirectory( "templates" ).
            addEntry( newFileEntry( "template1.txt", ByteSource.wrap( "some more data".getBytes() ) ) ).
            build();

        final Form config = Form.newForm().
            addFormItem( Input.newInput().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        final ContentTypeNames requiredCtypes = ContentTypeNames.from( "ctype1", "ctype2", "ctype3" );
        final ModuleKeys requiredModules = ModuleKeys.from( ModuleKey.from( "modA-1.0.0" ), ModuleKey.from( "modB-1.0.0" ) );

        return Module.newModule().
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
            addFileEntry( newModuleDirectory( "emptydir" ).build() ).
            build();
    }

    @Override
    protected Object getResourceInstance()
    {
        moduleService = Mockito.mock( ModuleService.class );

        final ModuleResource resource = new ModuleResource();
        resource.moduleService = moduleService;

        return resource;
    }

}
