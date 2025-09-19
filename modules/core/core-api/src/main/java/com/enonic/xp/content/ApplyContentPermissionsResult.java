package com.enonic.xp.content;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.acl.AccessControlList;

@PublicApi
public final class ApplyContentPermissionsResult
{
    private final Map<ContentId, AccessControlList> results;

    private ApplyContentPermissionsResult( Builder builder )
    {
        this.results = builder.results.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Map<ContentId, AccessControlList> getResults()
    {
        return results;
    }

    public AccessControlList getResult( final ContentId contentId )
    {
        return this.results.get( contentId );
    }

    public static final class Builder
    {
        private final ImmutableMap.Builder<ContentId, AccessControlList> results = ImmutableMap.builder();

        private Builder()
        {
        }

        public Builder addResult( ContentId contentId, AccessControlList permissions )
        {
            results.put( contentId, permissions );
            return this;
        }

        public ApplyContentPermissionsResult build()
        {
            return new ApplyContentPermissionsResult( this );
        }
    }
}
