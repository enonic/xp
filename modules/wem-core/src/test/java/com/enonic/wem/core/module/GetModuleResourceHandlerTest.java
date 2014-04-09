package com.enonic.wem.core.module;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.config.SystemConfig;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class GetModuleResourceHandlerTest
    extends AbstractCommandHandlerTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private GetModuleResourceHandler handler;

    private File modulesDir;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        this.modulesDir = this.temporaryFolder.newFolder( "modules" );

        final SystemConfig systemConfig = Mockito.mock( SystemConfig.class );
        when( systemConfig.getModulesDir() ).thenReturn( this.modulesDir.toPath() );
        handler = new GetModuleResourceHandler();
        handler.setContext( this.context );
        handler.setModuleResourcePathResolver( new ModuleResourcePathResolverImpl( systemConfig ) );

        createModule();
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
        final File file = new File( this.modulesDir, "modulename-1.0.0/public/javascript/controller.js" );
        assertTrue( file.getParentFile().mkdirs() );

        ByteSource.wrap( "some data".getBytes() ).copyTo( new FileOutputStream( file ) );
    }
}
