package com.enonic.xp.app;

import java.util.Objects;

import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.util.GenericValue;


public final class ApplicationDescriptor
{
    private final ApplicationKey key;

    private final String description;

    private final String descriptionI18nKey;

    private final Icon icon;

    private final GenericValue schemaConfig;

    private ApplicationDescriptor( final Builder builder )
    {
        this.key = Objects.requireNonNull( builder.key, "key cannot be null" );
        this.description = builder.description != null ? builder.description : "";
        this.descriptionI18nKey = builder.descriptionI18nKey;
        this.icon = builder.icon;
        this.schemaConfig = builder.schemaConfig.build();
    }

    public ApplicationKey getKey()
    {
        return key;
    }

    public String getDescription()
    {
        return description;
    }

    public String getDescriptionI18nKey()
    {
        return descriptionI18nKey;
    }

    public Icon getIcon()
    {
        return icon;
    }

    public GenericValue getSchemaConfig()
    {
        return schemaConfig;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( key, description, icon );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ApplicationKey key;

        private String description;

        private String descriptionI18nKey;

        private Icon icon;

        private final GenericValue.ObjectBuilder schemaConfig = GenericValue.newObject();

        private Builder()
        {
        }

        public Builder key( final ApplicationKey key )
        {
            this.key = key;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder description( final LocalizedText localizedText )
        {
            this.description = localizedText.text();
            this.descriptionI18nKey = localizedText.i18n();
            return this;
        }

        public Builder icon( final Icon icon )
        {
            this.icon = icon;
            return this;
        }

        public Builder schemaConfig( final GenericValue value )
        {
            value.properties().forEach( e -> this.schemaConfig.put( e.getKey(), e.getValue() ) );
            return this;
        }

        public ApplicationDescriptor build()
        {
            return new ApplicationDescriptor( this );
        }
    }
}
