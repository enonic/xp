package com.enonic.wem.api.schema;


import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.Name;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.schema.content.ContentTypeName;

public abstract class BaseSchema<T extends SchemaName>
implements Schema
{
    private final SchemaId id;

    private final SchemaName name;

    private final String displayName;

    private final DateTime createdTime;

    private final DateTime modifiedTime;

    private final UserKey creator;

    private final UserKey modifier;

    private final Icon icon;

    protected BaseSchema( Builder builder )
    {
        this.id = builder.id;
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.creator = builder.creator;
        this.modifier = builder.modifier;
        this.icon = builder.icon;
    }

    public SchemaId getId()
    {
        return id;
    }

    public String getName()
    {
        return name != null ? name.toString() : null;
    }

    public abstract T getQualifiedName();

    public String getDisplayName()
    {
        return displayName;
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

    public Icon getIcon()
    {
        return icon;
    }

    public static class Builder<T extends Builder>
    {
        private SchemaId id;

        private SchemaName name;

        private String displayName;

        private DateTime createdTime;

        private DateTime modifiedTime;

        private UserKey creator;

        private UserKey modifier;

        private Icon icon;

        public Builder()
        {

        }

        public Builder( final BaseSchema schema )
        {
            Preconditions.checkNotNull( schema, "schema cannot be null" );
            this.id = schema.id;
            this.name = schema.name;
            this.displayName = schema.displayName;
            this.createdTime = schema.createdTime;
            this.modifiedTime = schema.modifiedTime;
            this.creator = schema.creator;
            this.modifier = schema.modifier;
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

        public T name( final SchemaName value )
        {
            this.name = value;
            return getThis();
        }

        public T displayName( String value )
        {
            this.displayName = value;
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

        public T icon( Icon icon )
        {
            this.icon = icon;
            return getThis();
        }
    }
}
