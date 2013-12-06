package com.enonic.wem.api.content.page;

import java.util.Objects;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ResourcePath;

public abstract class DescriptorKey
{
    protected static final String SEPARATOR = ":";

    private final ModuleKey moduleKey;

    private final ResourcePath path;

    private final String refString;

    protected DescriptorKey( final ModuleKey moduleKey, final ResourcePath path )
    {
        this.moduleKey = moduleKey;
        this.path = path;
        this.refString = moduleKey.toString() + SEPARATOR + path.toString();
    }

    public ModuleKey getModuleKey()
    {
        return moduleKey;
    }

    public ResourcePath getPath()
    {
        return path;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final DescriptorKey that = (DescriptorKey) o;
        return Objects.equals( this.refString, that.refString );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.refString );
    }

    public String toString()
    {
        return refString;
    }
}
