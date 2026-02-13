package com.enonic.xp.content;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public final class GetActiveContentVersionsResult
{
    private final ImmutableMap<Branch, ContentVersion> contentVersions;

    private GetActiveContentVersionsResult( Builder builder )
    {
        contentVersions = builder.contentVersions.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Map<Branch, ContentVersion> getContentVersions()
    {
        return contentVersions;
    }

    public static final class Builder
    {
        private final ImmutableMap.Builder<Branch, ContentVersion> contentVersions = ImmutableMap.builder();

        private Builder()
        {
        }

        public Builder add( final Branch branch, final ContentVersion contentVersion )
        {
            this.contentVersions.put( branch, contentVersion );
            return this;
        }

        public GetActiveContentVersionsResult build()
        {
            return new GetActiveContentVersionsResult( this );
        }
    }
}
