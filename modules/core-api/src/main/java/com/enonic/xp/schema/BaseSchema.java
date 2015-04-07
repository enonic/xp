package com.enonic.xp.schema;


import java.time.Instant;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.icon.Icon;
import com.enonic.xp.module.ModuleBasedName;
import com.enonic.xp.security.PrincipalKey;

@Beta
public abstract class BaseSchema<T extends ModuleBasedName>
{
    final T name;

    final String displayName;

    final String description;

    final Instant createdTime;

    final Instant modifiedTime;

    final PrincipalKey creator;

    final PrincipalKey modifier;

    final Icon icon;

    protected BaseSchema( final Builder builder )
    {
        this.name = (T) builder.name;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.creator = builder.creator;
        this.modifier = builder.modifier;
        this.icon = builder.schemaIcon;
    }

    public T getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public Instant getCreatedTime()
    {
        return createdTime;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public PrincipalKey getCreator()
    {
        return creator;
    }

    public PrincipalKey getModifier()
    {
        return modifier;
    }

    public Icon getIcon()
    {
        return icon;
    }

    public static class Builder<T extends Builder, SCHEMA_NAME extends ModuleBasedName>
    {
        protected SCHEMA_NAME name;

        private String displayName;

        private String description;

        private Instant createdTime;

        private Instant modifiedTime;

        private PrincipalKey creator;

        private PrincipalKey modifier;

        private Icon schemaIcon;

        protected Builder()
        {
        }

        public Builder( final BaseSchema schema )
        {
            Preconditions.checkNotNull( schema, "schema cannot be null" );
            this.name = (SCHEMA_NAME) schema.name;
            this.displayName = schema.displayName;
            this.description = schema.description;
            this.createdTime = schema.createdTime;
            this.modifiedTime = schema.modifiedTime;
            this.creator = schema.creator;
            this.modifier = schema.modifier;
            this.schemaIcon = schema.icon;
        }

        private T getThis()
        {
            return (T) this;
        }

        public T name( final SCHEMA_NAME value )
        {
            this.name = value;
            return getThis();
        }

        public T displayName( String value )
        {
            this.displayName = value;
            return getThis();
        }

        public T description( String value )
        {
            this.description = value;
            return getThis();
        }

        public T createdTime( Instant value )
        {
            this.createdTime = value;
            return getThis();
        }

        public T modifiedTime( Instant value )
        {
            this.modifiedTime = value;
            return getThis();
        }

        public T creator( PrincipalKey value )
        {
            this.creator = value;
            return getThis();
        }

        public T modifier( PrincipalKey value )
        {
            this.modifier = value;
            return getThis();
        }

        public T icon( Icon schemaIcon )
        {
            this.schemaIcon = schemaIcon;
            return getThis();
        }
    }
}
