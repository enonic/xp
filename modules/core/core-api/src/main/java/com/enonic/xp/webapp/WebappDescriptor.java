package com.enonic.xp.webapp;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.api.ApiMountDescriptors;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

@PublicApi
public final class WebappDescriptor
{
    private static final String WEBAPP_DESCRIPTOR_PATH = "webapp/webapp.xml";

    private final ApplicationKey applicationKey;

    private final ApiMountDescriptors apiMounts;

    private WebappDescriptor( final Builder builder )
    {
        this.applicationKey = Objects.requireNonNull( builder.applicationKey );
        this.apiMounts = Objects.requireNonNullElse( builder.apiMounts, ApiMountDescriptors.empty() );
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public ApiMountDescriptors getApiMounts()
    {
        return apiMounts;
    }

    public static ResourceKey toResourceKey( final ApplicationKey applicationKey )
    {
        return ResourceKey.from( applicationKey, WEBAPP_DESCRIPTOR_PATH );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ApplicationKey applicationKey;

        private ApiMountDescriptors apiMounts;

        private Builder()
        {

        }

        public Builder applicationKey( final ApplicationKey applicationKey )
        {
            this.applicationKey = applicationKey;
            return this;
        }

        public Builder apiMounts( final ApiMountDescriptors apiMounts )
        {
            this.apiMounts = apiMounts;
            return this;
        }

        public WebappDescriptor build()
        {
            return new WebappDescriptor( this );
        }
    }
}
