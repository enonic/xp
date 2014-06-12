package com.enonic.wem.portal.underscore;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.base.Charsets;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.module.ModuleKeyResolver;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.core.module.ModuleKeyResolverService;
import com.enonic.wem.core.module.ModuleResourcePathResolver;
import com.enonic.wem.portal.base.BaseResourceTest;

public abstract class UnderscoreResourceTest<T extends UnderscoreResource>
    extends BaseResourceTest<T>
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    protected Path tmpDir;

    protected ModuleResourcePathResolver modulePathResolver;

    @Override
    protected void configure()
        throws Exception
    {
        this.tmpDir = this.temporaryFolder.getRoot().toPath();

        this.modulePathResolver = Mockito.mock( ModuleResourcePathResolver.class );
        this.resource.moduleKeyResolverService = Mockito.mock( ModuleKeyResolverService.class );
        this.resource.modulePathResolver = this.modulePathResolver;

        Mockito.when( this.modulePathResolver.resolveResourcePath( Mockito.isA( ModuleResourceKey.class ) ) ).thenReturn(
            this.tmpDir.resolve( "unknown" ) );

        Mockito.when( this.resource.moduleKeyResolverService.forContent( Mockito.isA( ContentPath.class ) ) ).thenReturn(
            ModuleKeyResolver.empty() );
    }

    protected final void addResource( final String name, final String key, final String content )
        throws Exception
    {
        final Path filePath = this.tmpDir.resolve( name );
        Files.write( filePath, content.getBytes( Charsets.UTF_8 ) );

        final ModuleResourceKey moduleResourceKey = ModuleResourceKey.from( key );
        Mockito.when( this.modulePathResolver.resolveResourcePath( moduleResourceKey ) ).thenReturn( filePath );
    }
}
