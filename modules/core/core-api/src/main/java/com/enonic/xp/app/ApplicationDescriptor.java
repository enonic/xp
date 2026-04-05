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

    private final String title;

    private final String titleI18nKey;

    private final String vendorName;

    private final String vendorUrl;

    private final String url;

    private ApplicationDescriptor( final Builder builder )
    {
        this.key = Objects.requireNonNull( builder.key, "key cannot be null" );
        this.description = builder.description != null ? builder.description : "";
        this.descriptionI18nKey = builder.descriptionI18nKey;
        this.icon = builder.icon;
        this.title = builder.title;
        this.titleI18nKey = builder.titleI18nKey;
        this.vendorName = builder.vendorName;
        this.vendorUrl = builder.vendorUrl;
        this.url = builder.url;
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

    public String getTitle()
    {
        return title;
    }

    public String getTitleI18nKey()
    {
        return titleI18nKey;
    }

    public String getVendorName()
    {
        return vendorName;
    }

    public String getVendorUrl()
    {
        return vendorUrl;
    }

    public String getUrl()
    {
        return url;
    }

    public GenericValue getSchemaConfig()
    {
        return schemaConfig;
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

        final ApplicationDescriptor that = (ApplicationDescriptor) o;

        return key.equals( that.key ) && Objects.equals( description, that.description ) && Objects.equals( icon, that.icon ) &&
            Objects.equals( descriptionI18nKey, that.descriptionI18nKey ) && Objects.equals( title, that.title ) &&
            Objects.equals( titleI18nKey, that.titleI18nKey ) && Objects.equals( vendorName, that.vendorName ) &&
            Objects.equals( vendorUrl, that.vendorUrl ) && Objects.equals( url, that.url ) && schemaConfig.equals( that.schemaConfig );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( key, description, icon, title, titleI18nKey, vendorName, vendorUrl, url, schemaConfig );
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

        private String title;

        private String titleI18nKey;

        private String vendorName;

        private String vendorUrl;

        private String url;

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

        public Builder title( final String title )
        {
            this.title = title;
            return this;
        }

        public Builder titleI18nKey( final String titleI18nKey )
        {
            this.titleI18nKey = titleI18nKey;
            return this;
        }

        public Builder title( final LocalizedText text )
        {
            this.title = text.text();
            this.titleI18nKey = text.i18n();
            return this;
        }

        public Builder vendorName( final String vendorName )
        {
            this.vendorName = vendorName;
            return this;
        }

        public Builder vendorUrl( final String vendorUrl )
        {
            this.vendorUrl = vendorUrl;
            return this;
        }

        public Builder url( final String url )
        {
            this.url = url;
            return this;
        }

        public ApplicationDescriptor build()
        {
            return new ApplicationDescriptor( this );
        }
    }
}
