package com.enonic.xp.schema;


import java.time.Instant;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.security.PrincipalKey;

@PublicApi
public abstract class BaseSchema<T extends BaseSchemaName>
{
    final T name;

    final String displayName;

    final String displayNameI18nKey;

    final String description;

    final String descriptionI18nKey;

    final Instant createdTime;

    final Instant modifiedTime;

    final PrincipalKey creator;

    final PrincipalKey modifier;

    final Icon icon;

    protected BaseSchema( final Builder builder )
    {
        this.name = (T) builder.name;
        this.displayName = builder.displayName == null || builder.displayName.isBlank() ? builder.name.getLocalName() : builder.displayName;
        this.displayNameI18nKey = builder.displayNameI18nKey;
        this.description = builder.description;
        this.descriptionI18nKey = builder.descriptionI18nKey;
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

    public String getDisplayNameI18nKey()
    {
        return displayNameI18nKey;
    }

    public String getDescription()
    {
        return description;
    }

    public String getDescriptionI18nKey()
    {
        return descriptionI18nKey;
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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final BaseSchema<?> that = (BaseSchema<?>) o;
        return Objects.equals( name, that.name ) && Objects.equals( displayName, that.displayName ) &&
            Objects.equals( displayNameI18nKey, that.displayNameI18nKey ) && Objects.equals( description, that.description ) &&
            Objects.equals( descriptionI18nKey, that.descriptionI18nKey ) && Objects.equals( createdTime, that.createdTime ) &&
            Objects.equals( modifiedTime, that.modifiedTime ) && Objects.equals( creator, that.creator ) &&
            Objects.equals( modifier, that.modifier ) && Objects.equals( icon, that.icon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, displayName, displayNameI18nKey, description, descriptionI18nKey, createdTime, modifiedTime, creator,
                             modifier, icon );
    }

    public static class Builder<T extends Builder, SCHEMA_NAME extends BaseSchemaName>
    {
        protected SCHEMA_NAME name;

        private String displayName;

        private String displayNameI18nKey;

        private String description;

        private String descriptionI18nKey;

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
            this.displayNameI18nKey = schema.displayNameI18nKey;
            this.description = schema.description;
            this.descriptionI18nKey = schema.descriptionI18nKey;
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

        public T displayNameI18nKey( String value )
        {
            this.displayNameI18nKey = value;
            return getThis();
        }

        public T description( String value )
        {
            this.description = value;
            return getThis();
        }

        public T descriptionI18nKey( final String descriptionI18nKey )
        {
            this.descriptionI18nKey = descriptionI18nKey;
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
