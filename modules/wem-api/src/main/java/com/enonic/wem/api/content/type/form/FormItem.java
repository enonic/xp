package com.enonic.wem.api.content.type.form;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.mixin.Mixin;

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

    public Input toInput()
    {
        if ( !( this instanceof Input ) )
        {
            throw new IllegalArgumentException( "This FormItem [" + getName() + "] is not an Input: " + this.getClass().getSimpleName() );
        }
        return (Input) this;
    }

    public FormItemSet toFormItemSet()
    {
        if ( !( this instanceof FormItemSet ) )
        {
            throw new IllegalArgumentException(
                "This FormItem [" + getName() + "] is not a FormItemSet: " + this.getClass().getSimpleName() );
        }
        return (FormItemSet) this;
    }

    public Layout toLayout()
    {
        if ( !( this instanceof Layout ) )
        {
            throw new IllegalArgumentException( "This FormItem [" + getName() + "] is not a Layout: " + this.getClass().getSimpleName() );
        }
        return (Layout) this;
    }

    @Override
    public String toString()
    {
        return name;
    }

    static FormItem from( final Mixin mixin, final MixinReference mixinReference )
    {
        final FormItem newFormItem = mixin.getFormItem().copy();
        newFormItem.setName( mixinReference.getName() );

        if ( newFormItem instanceof FormItemSet )
        {
            final FormItemSet newFormItemSet = (FormItemSet) newFormItem;
            newFormItemSet.setPath( mixinReference.getPath() );
        }
        else if ( newFormItem instanceof Input )
        {
            final Input newInput = (Input) newFormItem;
            newInput.setPath( mixinReference.getPath() );
        }
        return newFormItem;
    }
}
