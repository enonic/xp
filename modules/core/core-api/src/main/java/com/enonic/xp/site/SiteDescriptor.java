package com.enonic.xp.site;


import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.api.ApiMountDescriptors;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.site.mapping.ControllerMappingDescriptors;
import com.enonic.xp.site.processor.ResponseProcessorDescriptors;

@PublicApi
public final class SiteDescriptor
{
    private static final String SITE_DESCRIPTOR_PATH = "site/site.xml";

    private final ApplicationKey applicationKey;

    private final Form form;

    private final XDataMappings xDataMappings;

    private final ResponseProcessorDescriptors responseProcessors;

    private final ControllerMappingDescriptors mappingDescriptors;

    private final ApiMountDescriptors apiDescriptors;

    private final Instant modifiedTime;

    private SiteDescriptor( final Builder builder )
    {
        this.applicationKey = builder.applicationKey;
        this.form = builder.form;
        this.xDataMappings = builder.xDataMappings;
        this.modifiedTime = builder.modifiedTime;
        this.responseProcessors = Objects.requireNonNullElse( builder.responseProcessors, ResponseProcessorDescriptors.empty() );
        this.mappingDescriptors = Objects.requireNonNullElse( builder.mappingDescriptors, ControllerMappingDescriptors.empty() );
        this.apiDescriptors = Objects.requireNonNullElse( builder.apiDescriptors, ApiMountDescriptors.empty() );
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public Form getForm()
    {
        return form;
    }

    public XDataMappings getXDataMappings()
    {
        return xDataMappings;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public ResponseProcessorDescriptors getResponseProcessors()
    {
        return responseProcessors;
    }

    public ControllerMappingDescriptors getMappingDescriptors()
    {
        return mappingDescriptors;
    }

    public ApiMountDescriptors getApiDescriptors()
    {
        return apiDescriptors;
    }

    public static ResourceKey toResourceKey( final ApplicationKey applicationKey )
    {
        return ResourceKey.from( applicationKey, SITE_DESCRIPTOR_PATH );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static SiteDescriptor.Builder copyOf( final SiteDescriptor siteDescriptor )
    {
        return new Builder( siteDescriptor );
    }

    public static class Builder
    {
        private ApplicationKey applicationKey;

        private Form form;

        private XDataMappings xDataMappings;

        private Instant modifiedTime;

        private ResponseProcessorDescriptors responseProcessors;

        private ControllerMappingDescriptors mappingDescriptors;

        private ApiMountDescriptors apiDescriptors;

        private Builder()
        {
        }

        private Builder( final SiteDescriptor siteDescriptor )
        {
            this.applicationKey = siteDescriptor.applicationKey;
            this.form = siteDescriptor.form != null ? siteDescriptor.form.copy() : null;
            this.xDataMappings = siteDescriptor.xDataMappings;
            this.modifiedTime = siteDescriptor.modifiedTime;
            this.responseProcessors = siteDescriptor.responseProcessors;
            this.mappingDescriptors = siteDescriptor.mappingDescriptors;
            this.apiDescriptors = siteDescriptor.apiDescriptors;
        }

        public Builder applicationKey( final ApplicationKey applicationKey )
        {
            this.applicationKey = applicationKey;
            return this;
        }

        public Builder form( final Form form )
        {
            this.form = form;
            return this;
        }

        public Builder xDataMappings( final XDataMappings xDataMappings )
        {
            this.xDataMappings = xDataMappings;
            return this;
        }

        public Builder modifiedTime( final Instant modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return this;
        }

        public Builder responseProcessors( final ResponseProcessorDescriptors responseProcessors )
        {
            this.responseProcessors = responseProcessors;
            return this;
        }

        public Builder mappingDescriptors( final ControllerMappingDescriptors mappingDescriptors )
        {
            this.mappingDescriptors = mappingDescriptors;
            return this;
        }

        public Builder apiDescriptors( final ApiMountDescriptors apiDescriptors )
        {
            this.apiDescriptors = apiDescriptors;
            return this;
        }

        public SiteDescriptor build()
        {
            return new SiteDescriptor( this );
        }
    }
}
