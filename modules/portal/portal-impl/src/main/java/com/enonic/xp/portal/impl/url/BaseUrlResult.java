package com.enonic.xp.portal.impl.url;

import com.enonic.xp.site.Site;

final class BaseUrlResult
{
    private final String baseUrl;

    private final Site nearestSite;

    private BaseUrlResult( final Builder builder )
    {
        this.baseUrl = builder.baseUrl;
        this.nearestSite = builder.nearestSite;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public Site getNearestSite()
    {
        return nearestSite;
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private String baseUrl;

        private Site nearestSite;

        public Builder setBaseUrl( final String baseUrl )
        {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setNearestSite( final Site nearestSite )
        {
            this.nearestSite = nearestSite;
            return this;
        }

        public BaseUrlResult build()
        {
            return new BaseUrlResult( this );
        }
    }
}
