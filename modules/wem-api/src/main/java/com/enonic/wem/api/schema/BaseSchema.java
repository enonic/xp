package com.enonic.wem.api.schema;


import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;

public abstract class BaseSchema<T extends SchemaName>
    implements Schema
{
    final SchemaId id;

    final T name;

    final String displayName;

    final String description;

    final DateTime createdTime;

    final DateTime modifiedTime;

    final UserKey creator;

    final UserKey modifier;

    final SchemaIcon icon;

    protected BaseSchema( final Builder builder )
    {
        this.id = builder.id;
        this.name = (T) builder.name;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.creator = builder.creator;
        this.modifier = builder.modifier;
        this.icon = builder.schemaIcon;
    }

    public SchemaId getId()
    {
        return id;
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

    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    public DateTime getModifiedTime()
    {
        return modifiedTime;
    }

    public UserKey getCreator()
    {
        return creator;
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public SchemaIcon getIcon()
    {
        return icon;
    }

    public static class Builder<T extends Builder, SCHEMA_NAME extends SchemaName>
    {
        private SchemaId id;

        private SCHEMA_NAME name;

        private String displayName;

        private String description;

        private DateTime createdTime;

        private DateTime modifiedTime;

        private UserKey creator;

        private UserKey modifier;

        private SchemaIcon schemaIcon;

        public Builder()
        {

        }

        public Builder( final BaseSchema schema )
        {
            Preconditions.checkNotNull( schema, "schema cannot be null" );
            this.id = schema.id;
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

        public T id( final SchemaId value )
        {
            this.id = value;
            return getThis();
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

        public T createdTime( DateTime value )
        {
            this.createdTime = value;
            return getThis();
        }

        public T modifiedTime( DateTime value )
        {
            this.modifiedTime = value;
            return getThis();
        }

        public T creator( UserKey value )
        {
            this.creator = value;
            return getThis();
        }

        public T modifier( UserKey value )
        {
            this.modifier = value;
            return getThis();
        }

        public T icon( SchemaIcon schemaIcon )
        {
            this.schemaIcon = schemaIcon;
            return getThis();
        }
    }
}
