package com.enonic.xp.resource;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

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
}
