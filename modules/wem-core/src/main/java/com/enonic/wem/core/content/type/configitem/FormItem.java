package com.enonic.wem.core.content.type.configitem;


import org.elasticsearch.common.base.Preconditions;

public abstract class FormItem
{
    private String name;

    private FormItemType itemType;

    FormItem( final FormItemType itemType )
    {
        this.itemType = itemType;
    }

    public FormItemType getFormItemType()
    {
        return itemType;
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
            formItem.itemType = itemType;
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
