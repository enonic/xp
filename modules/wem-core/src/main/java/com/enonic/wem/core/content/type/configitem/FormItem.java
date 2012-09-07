package com.enonic.wem.core.content.type.configitem;


import org.elasticsearch.common.base.Preconditions;

public abstract class FormItem
{
    private String name;

    private ConfigItemType itemType;

    FormItem( final ConfigItemType itemType )
    {
        this.itemType = itemType;
    }

    public ConfigItemType getConfigItemType()
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
            throw new RuntimeException( "Failed to copy ConfigItem", e );
        }
        catch ( IllegalAccessException e )
        {
            throw new RuntimeException( "Failed to copy ConfigItem", e );
        }
    }

    @Override
    public String toString()
    {
        return name;
    }
}
