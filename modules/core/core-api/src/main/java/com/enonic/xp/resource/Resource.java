package com.enonic.xp.resource;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import com.google.common.io.ByteSource;

public interface Resource
{
    ResourceKey getKey();

    URL getUrl();

    void requireExists();

    boolean exists();

    long getSize();

    long getTimestamp();

    InputStream openStream();

    Readable openReader();

    String readString();

    byte[] readBytes();

    List<String> readLines();

    ByteSource getBytes();

    // TODO: Implement getBytes() -> ByteSource
    // TODO: Remove openReader
}
