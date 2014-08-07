package com.enonic.wem.api.resource;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public interface Resource
{
    public ResourceKey getKey();

    public URL getUrl();

    public boolean exists();

    public long getSize();

    public long getTimestamp();

    public InputStream openStream();

    public byte[] readBytes();

    public String readString();

    public List<String> readLines();
}
