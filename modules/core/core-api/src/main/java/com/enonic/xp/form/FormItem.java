package com.enonic.xp.form;


import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class FormItem
{
    private FormItems parent;

    public abstract String getName();

    public abstract FormItemType getType();

    void setParent( final FormItems parent )
    {
        this.parent = parent;
    }

    FormItems getParent()
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

    public FormFragment toFormFragment()
    {
        if ( !( this instanceof FormFragment ) )
        {
            throw new IllegalArgumentException(
                "This FormItem [" + getName() + "] is not an FormFragment: " + this.getClass().getSimpleName() );
        }
        return (FormFragment) this;
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

    public FieldSet toLayout()
    {
        if ( !( this instanceof FieldSet ) )
        {
            throw new IllegalArgumentException( "This FormItem [" + getName() + "] is not a Layout: " + this.getClass().getSimpleName() );
        }
        return (FieldSet) this;
    }

    public FormOptionSetOption toFormOptionSetOption()
    {
        if ( !( this instanceof FormOptionSetOption ) )
        {
            throw new IllegalArgumentException(
                "This FormItem [" + getName() + "] is not a FormOptionSetOption: " + this.getClass().getSimpleName() );
        }
        return (FormOptionSetOption) this;
    }

    public FormOptionSet toFormOptionSet()
    {
        if ( !( this instanceof FormOptionSet ) )
        {
            throw new IllegalArgumentException(
                "This FormItem [" + getName() + "] is not a FormOptionSet: " + this.getClass().getSimpleName() );
        }
        return (FormOptionSet) this;
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
}
