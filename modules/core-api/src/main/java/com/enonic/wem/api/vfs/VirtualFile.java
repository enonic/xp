package com.enonic.wem.api.vfs;

import java.net.URL;
import java.util.List;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

public interface VirtualFile
{
    public String getName();

    public String getPath();

    public URL getUrl();

    public boolean isFolder();

    public boolean isFile();

    public List<VirtualFile> getChildren();

    public CharSource getCharSource();

    public ByteSource getByteSource();

    public boolean exists();

    public VirtualFile resolve( final String path );
}
