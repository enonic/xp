package com.enonic.xp.portal.impl.resource.base;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.base.Charsets;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

public abstract class ApplicationBaseResourceTest
    extends BaseResourceTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    protected Path tmpDir;

    protected ApplicationService applicationService;

    protected ResourceService resourceService;

    protected final void configureApplicationService()
        throws Exception
    {
        this.tmpDir = this.temporaryFolder.getRoot().toPath();
        this.applicationService = Mockito.mock( ApplicationService.class );
        this.resourceService = Mockito.mock( ResourceService.class );
        this.services.setApplicationService( this.applicationService );
        this.services.setResourceService( this.resourceService );
    }

    protected final void addResource( final String name, final String key, final String content )
        throws Exception
    {
        final Path filePath = this.tmpDir.resolve( name );
        Files.write( filePath, content.getBytes( Charsets.UTF_8 ) );

        final ResourceKey applicationResourceKey = ResourceKey.from( key );
        final URL resourcePathUrl = filePath.toUri().toURL();
        final Resource resource = new Resource( applicationResourceKey, resourcePathUrl );
        Mockito.when( this.resourceService.getResource( applicationResourceKey ) ).thenReturn( resource );

    }
}
