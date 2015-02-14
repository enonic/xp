package com.enonic.xp.portal.impl.resource.base;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.base.Charsets;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.resource.ResourceKey;

public abstract class ModuleBaseResourceTest
    extends BaseResourceTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    protected Path tmpDir;

    protected ModuleService moduleService;

    protected final void configureModuleService()
        throws Exception
    {
        this.tmpDir = this.temporaryFolder.getRoot().toPath();
        this.moduleService = Mockito.mock( ModuleService.class );
        this.services.setModuleService( this.moduleService );
    }

    protected final void addResource( final String name, final String key, final String content )
        throws Exception
    {
        final Path filePath = this.tmpDir.resolve( name );
        Files.write( filePath, content.getBytes( Charsets.UTF_8 ) );

        final ResourceKey moduleResourceKey = ResourceKey.from( key );
        final Module module = Mockito.mock( Module.class );
        final URL resourcePathUrl = filePath.toUri().toURL();
        final String path = StringUtils.removeStart( moduleResourceKey.getPath(), "/" );
        Mockito.when( module.getResource( path ) ).thenReturn( resourcePathUrl );
        Mockito.when( this.moduleService.getModule( moduleResourceKey.getModule() ) ).thenReturn( module );
    }

    protected final void addModule( final String moduleName )
        throws Exception
    {
        final ModuleKey key = ModuleKey.from( moduleName );
        final Module module = Mockito.mock( Module.class );
        Mockito.when( module.getKey() ).thenReturn( key );
        Mockito.when( this.moduleService.getModule( key ) ).thenReturn( module );
    }
}
