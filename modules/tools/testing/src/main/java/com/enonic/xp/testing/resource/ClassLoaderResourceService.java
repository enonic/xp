package com.enonic.xp.testing.resource;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.vfs.VirtualFile;
import com.enonic.xp.vfs.VirtualFilePath;
import com.enonic.xp.vfs.VirtualFilePaths;

public final class ClassLoaderResourceService
    implements ResourceService
{
    private final ClassLoader loader;

    public ClassLoaderResourceService( final ClassLoader loader )
    {
        this.loader = loader;
    }

    @Override
    public Resource getResource( final ResourceKey key )
    {
        final URL url = this.loader.getResource( key.getPath().substring( 1 ) );
        return new UrlResource( key, url );
    }

    @Override
    public ResourceKeys findFiles( final ApplicationKey key, final String pattern )
    {
        throw new UnsupportedOperationException( "Not implemented" );
    }

    @Override
    public <K, V> V processResource( final ResourceProcessor<K, V> processor )
    {
        return processor.process( getResource( processor.toResourceKey() ) );
    }

    @Override
    public VirtualFile getVirtualFile( final ResourceKey resourceKey )
    {
        final URL url = this.loader.getResource( resourceKey.getPath().substring( 1 ) );
        return new VirtualFile()
        {
            @Override
            public String getName()
            {
                return resourceKey.getName();
            }

            @Override
            public VirtualFilePath getPath()
            {
                return VirtualFilePaths.from( resourceKey.getPath(), "/" );
            }

            @Override
            public URL getUrl()
            {
                return url;
            }

            @Override
            public boolean isFolder()
            {
                return url != null && url.getPath().endsWith( "/" );
            }

            @Override
            public boolean isFile()
            {
                return url != null && !url.getPath().endsWith( "/" );
            }

            @Override
            public List<VirtualFile> getChildren()
            {
                return List.of();
            }

            @Override
            public CharSource getCharSource()
            {
                return Resources.asCharSource( Objects.requireNonNull( url ), StandardCharsets.UTF_8 );
            }

            @Override
            public ByteSource getByteSource()
            {
                return Resources.asByteSource( Objects.requireNonNull( url ) );
            }

            @Override
            public boolean exists()
            {
                return url != null;
            }

            @Override
            public VirtualFile resolve( final VirtualFilePath path )
            {
                return ClassLoaderResourceService.this.getVirtualFile( resourceKey.resolve( path.getPath() ) );
            }
        };
    }
}
