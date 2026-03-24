package com.enonic.xp.webapp;

import java.util.Objects;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.schema.LocalizedText;


public final class WebappDescriptor
{
    private final ApplicationKey applicationKey;

    private final DescriptorKeys apiMounts;

    private final String description;

    private final String descriptionI18nKey;

    private WebappDescriptor( final Builder builder )
    {
        this.applicationKey = Objects.requireNonNull( builder.applicationKey );
        this.apiMounts = Objects.requireNonNullElse( builder.apiMounts, DescriptorKeys.empty() );
        this.description = builder.description;
        this.descriptionI18nKey = builder.descriptionI18nKey;
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public DescriptorKeys getApiMounts()
    {
        return apiMounts;
    }

    public String getDescription()
    {
        return description;
    }

    public String getDescriptionI18nKey()
    {
        return descriptionI18nKey;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ApplicationKey applicationKey;

        private DescriptorKeys apiMounts;

        private String description;

        private String descriptionI18nKey;

        private Builder()
        {

        }

        public Builder applicationKey( final ApplicationKey applicationKey )
        {
            this.applicationKey = applicationKey;
            return this;
        }

        public Builder apiMounts( final DescriptorKeys apiMounts )
        {
            this.apiMounts = apiMounts;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder descriptionI18nKey( final String descriptionI18nKey )
        {
            this.descriptionI18nKey = descriptionI18nKey;
            return this;
        }

        public Builder description( final LocalizedText text )
        {
            this.description = text.text();
            this.descriptionI18nKey = text.i18n();
            return this;
        }

        public WebappDescriptor build()
        {
            return new WebappDescriptor( this );
        }
    }
}
