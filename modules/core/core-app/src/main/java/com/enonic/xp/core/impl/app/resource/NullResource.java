package com.enonic.xp.core.impl.app.resource;

import java.net.URL;
import java.util.List;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import com.enonic.xp.vfs.VirtualFile;
import com.enonic.xp.vfs.VirtualFilePath;
import com.enonic.xp.vfs.VirtualFilePaths;

final class NullResource
    implements VirtualFile
{
    private final String path;

    private final VirtualFilePath virtualFilePath;

    NullResource( final String path )
    {
        this.virtualFilePath = VirtualFilePaths.from( path, "/" );
        this.path = path;
    }

    NullResource( final VirtualFilePath virtualFilePath )
    {
        this.virtualFilePath = virtualFilePath;
        this.path = virtualFilePath.getPath();
    }

    @Override
    public String getName()
    {
        if ( this.path.equals( "/" ) )
        {
            return "";
        }
        else
        {
            return this.path.substring( this.path.lastIndexOf( '/' ) + 1 );
        }
    }

    @Override
    public VirtualFilePath getPath()
    {
        return this.virtualFilePath;
    }

    @Override
    public URL getUrl()
    {
        return null;
    }

    @Override
    public boolean isFolder()
    {
        return false;
    }

    @Override
    public boolean isFile()
    {
        return false;
    }

    @Override
    public List<VirtualFile> getChildren()
    {
        return List.of();
    }

    @Override
    public CharSource getCharSource()
    {
        return null;
    }

    @Override
    public ByteSource getByteSource()
    {
        return null;
    }

    @Override
    public boolean exists()
    {
        return false;
    }

    @Override
    public VirtualFile resolve( final VirtualFilePath path )
    {
        return new NullResource( path.getPath() );
    }
}
