package com.enonic.wem.core.module;


import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.CreateModuleResource;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.CreateModuleParams;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.config.SystemConfig;

import static com.enonic.wem.api.module.ModuleFileEntry.newModuleDirectory;
import static com.enonic.wem.api.resource.Resource.newResource;
import static com.google.common.io.ByteStreams.asByteSource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class CreateModuleResourceHandlerTest
    extends AbstractCommandHandlerTest
{
    private CreateModuleResourceHandler handler;

    private SystemConfig systemConfig;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        systemConfig = Mockito.mock( SystemConfig.class );
        when( systemConfig.getModulesDir() ).thenReturn( java.nio.file.Files.createTempDirectory( "module" ) );
        handler = new CreateModuleResourceHandler();
        handler.setContext( this.context );
        handler.setModuleResourcePathResolver( new ModuleResourcePathResolverImpl( systemConfig ) );

        createModule();
    }

    @After
    public void deleteTempDir()
    {
        try
        {
            FileUtils.deleteDirectory( systemConfig.getModulesDir().toFile() );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    @Test
    public void createModuleResource()
        throws Exception
    {
        // setup
        final ResourcePath path = ResourcePath.from( "other/folder/myfile.js" );
        final ModuleKey moduleKey = ModuleKey.from( "modulename-1.0.0" );
        final ModuleResourceKey resourceKey = new ModuleResourceKey( moduleKey, path );
        final Resource resource = newResource().name( "myfile.js" ).stringValue( "test data" ).build();
        final CreateModuleResource command = Commands.module().createResource().resourceKey( resourceKey ).resource( resource );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        final Resource resourceResult = command.getResult();
        assertNotNull( resourceResult );
        assertEquals( "myfile.js", resourceResult.getName() );
    }

    @Test(expected = ModuleNotFoundException.class)
    public void createResourceModuleNotFound()
        throws Exception
    {
        // setup
        final ResourcePath path = ResourcePath.from( "public/javascript/controller.js" );
        final ModuleKey moduleKey = ModuleKey.from( "othermodule-1.0.0" );
        final ModuleResourceKey resourceKey = new ModuleResourceKey( moduleKey, path );
        final Resource resource = newResource().name( "controller.js" ).stringValue( "test data" ).build();
        final CreateModuleResource command = Commands.module().createResource().resourceKey( resourceKey ).resource( resource );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();
    }

    public void createModule()
        throws Exception
    {
        final Form config = Form.newForm().
            addFormItem( Input.newInput().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
        final ModuleFileEntry.Builder directoryBuilder = newModuleDirectory( "public" ).
            addFile( "resource1.txt", asByteSource( "data1".getBytes() ) ).
            addFile( "resource2.txt", asByteSource( "data2".getBytes() ) ).
            addFile( "resource3.txt", asByteSource( "data3".getBytes() ) );
        final ModuleFileEntry.Builder subDirectory = newModuleDirectory( "javascript" ).
            addFile( "controller.js", asByteSource( "some data".getBytes() ) ).
            addFile( "helper.js", asByteSource( "more data".getBytes() ) );
        final ModuleFileEntry moduleDirectoryEntry = ModuleFileEntry.newModuleDirectory( "" ).
            addEntry( directoryBuilder.addEntry( subDirectory ) ).
            build();
        final CreateModuleParams params = new CreateModuleParams().
            name( "modulename" ).
            version( ModuleVersion.from( 1, 0, 0 ) ).
            displayName( "module display name" ).
            info( "module-info" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            minSystemVersion( ModuleVersion.from( 5, 0, 0 ) ).
            maxSystemVersion( ModuleVersion.from( 6, 0, 0 ) ).
            moduleDependencies( ModuleKeys.from( "modulefoo-1.0.0", "modulebar-1.2.3" ) ).
            contentTypeDependencies( ContentTypeNames.from( "article" ) ).
            moduleDirectoryEntry( moduleDirectoryEntry ).
            config( config );

        final CreateModuleCommand createModuleHandler = new CreateModuleCommand();
        createModuleHandler.systemConfig( systemConfig );
        createModuleHandler.moduleExporter( new ModuleExporter() );
        createModuleHandler.params( params );
        createModuleHandler.execute();
    }
}
