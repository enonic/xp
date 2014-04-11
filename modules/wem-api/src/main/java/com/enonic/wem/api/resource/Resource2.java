package com.enonic.wem.api.resource;

import java.net.URL;

import com.google.common.io.ByteSource;

public interface Resource2
{
    public ResourceKey getKey();

    public URL getResolvedUrl();

    public long getSize();

    public long getTimestamp();

    public ByteSource getByteSource();

    public String getAsString();
}
