package com.enonic.wem.api.content;

import java.util.Map;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

import com.enonic.wem.api.entity.Workspace;

public class GetActiveContentVersionsResult
{
    private ImmutableSortedMap<Workspace, ContentVersion> contentVersions;

    private GetActiveContentVersionsResult( Builder builder )
    {
        // Order map by ordering of contentVersions, then WorkspaceName in case same version
        final Ordering<Workspace> workspaceOrdering = Ordering.
            natural().
            onResultOf( Functions.forMap( builder.contentVersions ) ).
            compound( Ordering.usingToString() );

        contentVersions = ImmutableSortedMap.copyOf( builder.contentVersions, workspaceOrdering );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ImmutableSortedMap<Workspace, ContentVersion> getContentVersions()
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
            if ( contentVersion != null )
            {
                this.contentVersions.put( workspace, contentVersion );
            }
            return this;
        }

        public GetActiveContentVersionsResult build()
        {
            return new GetActiveContentVersionsResult( this );
        }
    }
}
