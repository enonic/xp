package com.enonic.wem.api.form;


import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;

public abstract class FormItem
{
    private FormItems parent;

    public abstract String getName();

    public abstract FormItemType getType();

    void setParent( final FormItems parent )
    {
        this.parent = parent;
    }

    public FormItems getParent()
    {
        return parent;
    }

    public final FormItemPath getPath()
    {
        return resolvePath();
    }

    FormItemPath resolvePath()
    {
        return FormItemPath.from( resolveParentPath(), getName() );
    }

    final FormItemPath resolveParentPath()
    {
        if ( parent == null )
        {
            return FormItemPath.ROOT;
        }
        else
        {
            return parent.getPath();
        }
    }

    public abstract FormItem copy();

    public Input toInput()
    {
        if ( !( this instanceof Input ) )
        {
            throw new IllegalArgumentException( "This FormItem [" + getName() + "] is not an Input: " + this.getClass().getSimpleName() );
        }
        return (Input) this;
    }

    public Inline toInline()
    {
        if ( !( this instanceof Inline ) )
        {
            throw new IllegalArgumentException(
                "This FormItem [" + getName() + "] is not an Inline: " + this.getClass().getSimpleName() );
        }
        return (Inline) this;
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
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof FormItem ) )
        {
            return false;
        }

        final FormItem that = (FormItem) o;
        return Objects.equals( getName(), that.getName() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getName() );
    }

    @Override
    public String toString()
    {
        return getName();
    }

    static FormItem from( final FormItem formItem )
    {
        final FormItem newFormItem;
        if ( formItem instanceof FormItemSet )
        {
            newFormItem = newFormItemSet( (FormItemSet) formItem ).build();
        }
        else if ( formItem instanceof Input )
        {
            newFormItem = newInput( (Input) formItem ).build();
        }
        else
        {
            throw new IllegalArgumentException(
                "Cannot create FormItem [" + formItem.getPath().toString() + "] of type: " + formItem.getClass().getSimpleName() );
        }
        return newFormItem;
    }
}
