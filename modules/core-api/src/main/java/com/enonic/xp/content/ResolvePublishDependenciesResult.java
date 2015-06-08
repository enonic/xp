package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class ResolvePublishDependenciesResult
{
    private final PushContentRequests pushContentRequests;

    private ResolvePublishDependenciesResult( final Builder builder )
    {
        this.pushContentRequests = builder.pushContentRequests;
    }

    public PushContentRequests getPushContentRequests()
    {
        return pushContentRequests;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private PushContentRequests pushContentRequests;

        private Builder()
        {
        }

        public Builder pushContentRequests( final PushContentRequests pushContentRequests )
        {
            this.pushContentRequests = pushContentRequests;
            return this;
        }

        public ResolvePublishDependenciesResult build()
        {
            return new ResolvePublishDependenciesResult( this );
        }
    }

}
