package com.enonic.xp.vfs;

import java.net.URL;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

class NullResource
    implements VirtualFile
{
    @Override
    public String getName()
    {
        return null;
    }

    @Override
    public VirtualFilePath getPath()
    {
        return null;
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
        return Lists.newArrayList();
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
        return new NullResource();
    }
}