package com.enonic.wem.api.content.page;

public final class ComponentDescriptorName
{
    private final String name;

    private ComponentDescriptorName( final String name )
    {
        this.name = name;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ComponentDescriptorName ) && ( (ComponentDescriptorName) o ).name.equals( this.name );
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public String toString()
    {
        return name;
    }

    public static ComponentDescriptorName from( final String name )
    {
        return new ComponentDescriptorName( name );
    }
}
