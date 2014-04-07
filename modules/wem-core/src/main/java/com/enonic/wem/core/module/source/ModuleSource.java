package com.enonic.wem.core.module.source;

import java.net.URL;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.module.ModuleResourceKey;

public interface ModuleSource
{
    public String getUri();

    public ModuleResourceKey getKey();

    public boolean exists();

    public URL getResolvedUrl();

    public ByteSource getBytes();

    public long getTimestamp();
}
