package com.enonic.xp.schema;


import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.icon.Icon;
import com.enonic.xp.security.PrincipalKey;

import static java.util.Objects.requireNonNull;


public abstract class BaseSchema<T extends BaseSchemaName>
{
    final T name;

    final String title;

    final String titleI18nKey;

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
        this.title = builder.title == null || builder.title.isBlank() ? builder.name.getLocalName() : builder.title;
        this.titleI18nKey = builder.titleI18nKey;
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

    public String getTitle()
    {
        return title;
    }

    public String getTitleI18nKey()
    {
        return titleI18nKey;
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
        return Objects.equals( name, that.name ) && Objects.equals( title, that.title ) &&
            Objects.equals( titleI18nKey, that.titleI18nKey ) && Objects.equals( description, that.description ) &&
            Objects.equals( descriptionI18nKey, that.descriptionI18nKey ) && Objects.equals( createdTime, that.createdTime ) &&
            Objects.equals( modifiedTime, that.modifiedTime ) && Objects.equals( creator, that.creator ) &&
            Objects.equals( modifier, that.modifier ) && Objects.equals( icon, that.icon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, title, titleI18nKey, description, descriptionI18nKey, createdTime, modifiedTime, creator,
                             modifier, icon );
    }

    public static class Builder<T extends Builder, SCHEMA_NAME extends BaseSchemaName>
    {
        protected SCHEMA_NAME name;

        private String title;

        private String titleI18nKey;

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
            requireNonNull( schema, "BaseSchema cannot be null" );
            this.name = (SCHEMA_NAME) schema.name;
            this.title = schema.title;
            this.titleI18nKey = schema.titleI18nKey;
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

        public T title( String value )
        {
            this.title = value;
            return getThis();
        }

        public T titleI18nKey( String value )
        {
            this.titleI18nKey = value;
            return getThis();
        }

        public T title( LocalizedText text )
        {
            this.title = text.text();
            this.titleI18nKey = text.i18n();
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

        public T description( LocalizedText text )
        {
            this.description = text.text();
            this.descriptionI18nKey = text.i18n();
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
