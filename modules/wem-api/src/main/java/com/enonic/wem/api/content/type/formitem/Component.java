package com.enonic.wem.api.content.type.formitem;


import com.google.common.base.Preconditions;

public abstract class Component
{
    private String name;

    Component()
    {
    }

    void setName( final String name )
    {
        Preconditions.checkArgument( !name.contains( "." ), "name cannot contain punctations: " + name );
        this.name = name;
    }

    public final String getName()
    {
        return name;
    }

    public Component copy()
    {
        try
        {
            Component component = this.getClass().newInstance();
            component.name = name;
            return component;
        }
        catch ( InstantiationException e )
        {
            throw new RuntimeException( "Failed to copy Component", e );
        }
        catch ( IllegalAccessException e )
        {
            throw new RuntimeException( "Failed to copy Component", e );
        }
    }

    @Override
    public String toString()
    {
        return name;
    }
}
