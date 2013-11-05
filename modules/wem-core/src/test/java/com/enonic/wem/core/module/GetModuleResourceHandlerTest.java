package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.CreateModule;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.config.SystemConfig;

import static com.enonic.wem.api.module.ModuleFileEntry.directoryBuilder;
import static com.google.common.io.ByteStreams.asByteSource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class GetModuleResourceHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetModuleResourceHandler handler;

    private SystemConfig systemConfig;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        systemConfig = Mockito.mock( SystemConfig.class );
        when( systemConfig.getModuleDir() ).thenReturn( java.nio.file.Files.createTempDirectory( "module" ).toFile() );
        handler = new GetModuleResourceHandler();
        handler.setContext( this.context );
        handler.setSystemConfig( systemConfig );

        createModule();
    }

    @After
    public void deleteTempDir()
    {
        try
        {
            FileUtils.deleteDirectory( systemConfig.getModuleDir() );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    @Test
    public void getModuleResource()
        throws Exception
    {
        // setup
        final ResourcePath path = ResourcePath.from( "public/javascript/controller.js" );
        final ModuleKey moduleKey = ModuleKey.from( "modulename-1.0.0" );
        final ModuleResourceKey resourceKey = new ModuleResourceKey( moduleKey, path );
        final GetModuleResource command = Commands.module().getResource().resourceKey( resourceKey );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        final Resource resource = command.getResult();
        assertNotNull( resource );
        assertEquals( "controller.js", resource.getName() );
        assertEquals( 9, resource.getSize() );
        assertArrayEquals( "some data".getBytes( Charset.forName( "UTF-8" ) ), resource.getByteSource().read() );
    }

    @Test(expected = ModuleNotFoundException.class)
    public void getResourceModuleNotFound()
        throws Exception
    {
        // setup
        final ResourcePath path = ResourcePath.from( "public/javascript/controller.js" );
        final ModuleKey moduleKey = ModuleKey.from( "othermodule-1.0.0" );
        final ModuleResourceKey resourceKey = new ModuleResourceKey( moduleKey, path );
        final GetModuleResource command = Commands.module().getResource().resourceKey( resourceKey );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getResourceNotFound()
        throws Exception
    {
        // setup
        final ResourcePath path = ResourcePath.from( "public/javascript/missing.file" );
        final ModuleKey moduleKey = ModuleKey.from( "modulename-1.0.0" );
        final ModuleResourceKey resourceKey = new ModuleResourceKey( moduleKey, path );
        final GetModuleResource command = Commands.module().getResource().resourceKey( resourceKey );

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
        final ModuleFileEntry.Builder directoryBuilder = directoryBuilder( "public" ).
            addFile( "resource1.txt", asByteSource( "data1".getBytes() ) ).
            addFile( "resource2.txt", asByteSource( "data2".getBytes() ) ).
            addFile( "resource3.txt", asByteSource( "data3".getBytes() ) );
        final ModuleFileEntry.Builder subDirectory = directoryBuilder( "javascript" ).
            addFile( "controller.js", asByteSource( "some data".getBytes() ) ).
            addFile( "helper.js", asByteSource( "more data".getBytes() ) );
        final ModuleFileEntry moduleDirectoryEntry = ModuleFileEntry.directoryBuilder( "" ).
            addEntry( directoryBuilder.addEntry( subDirectory ) ).
            build();
        final CreateModule command = Commands.module().create().
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

        final CreateModuleHandler createModuleHandler = new CreateModuleHandler();
        createModuleHandler.setContext( this.context );
        createModuleHandler.setSystemConfig( systemConfig );
        createModuleHandler.setModuleExporter( new ModuleExporter() );
        createModuleHandler.setCommand( command );
        createModuleHandler.handle();
    }
}
