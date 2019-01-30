package com.enonic.xp.form;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

public abstract class NamedFormItem
    extends FormItem
{
    private final String name;

    NamedFormItem( final String name )
    {
        super();

        Preconditions.checkNotNull( name, "a name is required for a " + this.getClass().getSimpleName() );
        Preconditions.checkArgument( StringUtils.isNotBlank( name ), "a name is required for a " + this.getClass().getSimpleName() );
        Preconditions.checkArgument( !name.contains( "." ), "name cannot contain punctations: " + name );
        this.name = name;
    }

    public String getName()
    {
        return name;
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
        if ( this.getParent() == null )
        {
            return FormItemPath.ROOT;
        }
        else
        {
            return this.getParent().getPath();
        }
    }

    @Override
    public String toString()
    {
        return getName();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getName() );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof NamedFormItem ) )
        {
            return false;
        }

        final NamedFormItem that = (NamedFormItem) o;
        return Objects.equals( getName(), that.getName() );
    }

    public static class Builder<B extends Builder>
    {
        protected String name;

        protected Builder()
        {
            // default
        }

        protected Builder( final NamedFormItem source )
        {
            this.name = source.getName();
        }

        public B name( String value )
        {
            name = value;
            return (B) this;
        }
    }
}
