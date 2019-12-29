package com.enonic.xp.vfs;

import java.net.URL;
import java.util.List;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface VirtualFile
{
    String getName();

    VirtualFilePath getPath();

    URL getUrl();

    boolean isFolder();

    boolean isFile();

    List<VirtualFile> getChildren();

    CharSource getCharSource();

    ByteSource getByteSource();

    boolean exists();

    VirtualFile resolve( final VirtualFilePath path );
}
