package com.enonic.wem.api.content;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.entity.Workspace;

public class GetActiveContentVersionsResult
{
    private ImmutableMap<Workspace, ContentVersion> contentVersions;

    private GetActiveContentVersionsResult( Builder builder )
    {
        contentVersions = ImmutableMap.copyOf( builder.contentVersions );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ImmutableMap<Workspace, ContentVersion> getContentVersions()
    {
        return contentVersions;
    }

    public static final class Builder
    {
        private Map<Workspace, ContentVersion> contentVersions = Maps.newHashMap();

        private Builder()
        {
        }

        public Builder add( final Workspace workspace, final ContentVersion contentVersion )
        {
            this.contentVersions.put( workspace, contentVersion );
            return this;
        }

        public GetActiveContentVersionsResult build()
        {
            return new GetActiveContentVersionsResult( this );
        }
    }
}
