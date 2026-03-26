package com.enonic.xp.site;


import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.site.mapping.ControllerMappingDescriptors;
import com.enonic.xp.site.processor.ResponseProcessorDescriptors;
import com.enonic.xp.util.GenericValue;

import static java.util.Objects.requireNonNullElse;


public final class SiteDescriptor
{
    private final ApplicationKey applicationKey;

    private final ResponseProcessorDescriptors responseProcessors;

    private final ControllerMappingDescriptors mappingDescriptors;

    private final DescriptorKeys apiMounts;

    private final Instant modifiedTime;

    private final GenericValue schemaConfig;

    private SiteDescriptor( final Builder builder )
    {
        this.applicationKey = Objects.requireNonNull( builder.applicationKey );
        this.modifiedTime = builder.modifiedTime;
        this.responseProcessors = requireNonNullElse( builder.responseProcessors, ResponseProcessorDescriptors.empty() );
        this.mappingDescriptors = requireNonNullElse( builder.mappingDescriptors, ControllerMappingDescriptors.empty() );
        this.apiMounts = requireNonNullElse( builder.apiMounts, DescriptorKeys.empty() );
        this.schemaConfig = builder.schemaConfig.build();
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
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

    public DescriptorKeys getApiMounts()
    {
        return apiMounts;
    }

    public GenericValue getSchemaConfig()
    {
        return schemaConfig;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static SiteDescriptor.Builder copyOf( final SiteDescriptor siteDescriptor )
    {
        return new Builder( siteDescriptor );
    }

    public static final class Builder
    {
        private ApplicationKey applicationKey;

        private Instant modifiedTime;

        private ResponseProcessorDescriptors responseProcessors;

        private ControllerMappingDescriptors mappingDescriptors;

        private DescriptorKeys apiMounts;

        private final GenericValue.ObjectBuilder schemaConfig = GenericValue.newObject();

        private Builder()
        {
        }

        private Builder( final SiteDescriptor siteDescriptor )
        {
            this.applicationKey = siteDescriptor.applicationKey;
            this.modifiedTime = siteDescriptor.modifiedTime;
            this.responseProcessors = siteDescriptor.responseProcessors;
            this.mappingDescriptors = siteDescriptor.mappingDescriptors;
            this.apiMounts = siteDescriptor.apiMounts;
            this.schemaConfig( siteDescriptor.schemaConfig );
        }

        public Builder applicationKey( final ApplicationKey applicationKey )
        {
            this.applicationKey = applicationKey;
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

        public Builder apiMounts( final DescriptorKeys apiMounts )
        {
            this.apiMounts = apiMounts;
            return this;
        }

        public Builder schemaConfig( final GenericValue value )
        {
            value.properties().forEach( e -> this.schemaConfig.put( e.getKey(), e.getValue() ) );
            return this;
        }

        public SiteDescriptor build()
        {
            return new SiteDescriptor( this );
        }
    }
}
