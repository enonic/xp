package com.enonic.xp.script.impl.function;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.google.common.io.Files;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;

public class ResolverTestSupport
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    protected ResourceService resourceService;

    @Before
    public final void setup()
    {
        this.resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( this.resourceService.getResource( Mockito.any() ) ).then( this::loadResource );
    }

    private Resource loadResource( final InvocationOnMock invocation )
        throws Exception
    {
        return loadResource( (ResourceKey) invocation.getArguments()[0] );
    }

    private Resource loadResource( final ResourceKey key )
        throws Exception
    {
        final File file = new File( this.temporaryFolder.getRoot(), key.getPath() );
        return new UrlResource( key, file.toURI().toURL() );
    }

    final void touchFile( final String path )
        throws Exception
    {
        final File file = new File( this.temporaryFolder.getRoot(), path );
        file.getParentFile().mkdirs();
        Files.touch( file );
    }
}
