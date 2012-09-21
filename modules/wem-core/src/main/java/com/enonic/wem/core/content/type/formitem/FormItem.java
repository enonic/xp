package com.enonic.wem.core.content.type.formitem;


import org.elasticsearch.common.base.Preconditions;

public abstract class FormItem
{
    private String name;

    FormItem()
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

    public FormItem copy()
    {
        try
        {
            FormItem formItem = this.getClass().newInstance();
            formItem.name = name;
            return formItem;
        }
        catch ( InstantiationException e )
        {
            throw new RuntimeException( "Failed to copy FormItem", e );
        }
        catch ( IllegalAccessException e )
        {
            throw new RuntimeException( "Failed to copy FormItem", e );
        }
    }

    @Override
    public String toString()
    {
        return name;
    }
}
