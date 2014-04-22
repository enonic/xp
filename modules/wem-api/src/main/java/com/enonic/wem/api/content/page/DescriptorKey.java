package com.enonic.wem.api.content.page;

import java.util.Objects;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;

public abstract class DescriptorKey
{
    public enum DescriptorType
    {
        IMAGE,
        LAYOUT,
        PAGE,
        PART
    }

    protected static final String SEPARATOR = ":";

    private final ModuleKey moduleKey;

    private final ComponentDescriptorName name;

    private final String refString;

    private final DescriptorType descriptorType;

    protected DescriptorKey( final ModuleKey moduleKey, final ComponentDescriptorName name, final DescriptorType descriptorType )
    {
        this.descriptorType = descriptorType;
        this.moduleKey = moduleKey;
        this.name = name;
        this.refString = moduleKey.toString() + SEPARATOR + name.toString();
    }

    public ModuleKey getModuleKey()
    {
        return moduleKey;
    }

    public ComponentDescriptorName getName()
    {
        return name;
    }

    public DescriptorType getDescriptorType()
    {
        return descriptorType;
    }

    public abstract ModuleResourceKey toResourceKey();

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
