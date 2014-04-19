package com.enonic.wem.api.resource;

import java.util.List;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.module.ModuleResourceKey;

public interface Resource
{
    public ModuleResourceKey getKey();

    public long getSize();

    public long getTimestamp();

    public ByteSource getByteSource();

    public String readAsString();

    public List<String> readLines();
}
