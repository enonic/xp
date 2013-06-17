package com.enonic.wem.api.resource;


import java.io.File;

import com.google.common.io.ByteSource;

public class Resource
{
    private String name;

    private ByteSource byteSource;

    private String postfix;

    private long size;

    private File file;

    public Resource( final String name, final ByteSource byteSource )
    {
        this.name = name;
        this.byteSource = byteSource;
    }

    public String getPostfix()
    {
        return postfix;
    }

    public void setPostfix( final String postfix )
    {
        this.postfix = postfix;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize( final long size )
    {
        this.size = size;
    }

    public File getFile()
    {
        return file;
    }

    public void setFile( final File file )
    {
        this.file = file;
    }

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public ByteSource getByteSource()
    {
        return byteSource;
    }

    public void setByteSource( final ByteSource byteSource )
    {
        this.byteSource = byteSource;
    }

}
