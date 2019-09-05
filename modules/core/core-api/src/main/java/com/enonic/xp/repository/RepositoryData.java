package com.enonic.xp.repository;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexType;
import com.google.common.annotations.Beta;

import java.util.Objects;

@Beta
public class RepositoryData
{
    private final PropertyTree value;

    private RepositoryData( PropertyTree value )
    {
        this.value = value.copy();
    }

    public PropertyTree getValue()
    {
        return value;
    }

    public static RepositoryData create( PropertyTree value) {
        return new RepositoryData(value);
    }
}
