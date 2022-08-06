package com.enonic.xp.resource;

import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.List;

import com.google.common.io.ByteSource;

public interface Resource
{
    ResourceKey getKey();

    @Deprecated
    URL getUrl();

    void requireExists();

    boolean exists();

    long getSize();

    long getTimestamp();

    InputStream openStream();

    Reader openReader();

    String readString();

    byte[] readBytes();

    List<String> readLines();

    ByteSource getBytes();

    ResourceResolver getResolver();
}
