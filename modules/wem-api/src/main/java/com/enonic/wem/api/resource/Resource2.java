package com.enonic.wem.api.resource;

import java.util.List;

import com.google.common.io.ByteSource;

public interface Resource2
{
    public String getUri();

    public ResourceReference getReference();

    public long getSize();

    public long getTimestamp();

    public ByteSource getByteSource();

    public String readAsString();

    public List<String> readLines();
}
