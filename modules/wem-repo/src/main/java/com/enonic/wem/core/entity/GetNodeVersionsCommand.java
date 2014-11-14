package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.core.version.GetVersionsQuery;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.repo.FindNodeVersionsResult;
import com.enonic.wem.repo.NodeId;

public class GetNodeVersionsCommand
{
    private final NodeId nodeId;

    private final int from;

    private final int size;

    private final VersionService versionService;

    private GetNodeVersionsCommand( Builder builder )
    {
        nodeId = builder.nodeId;
        from = builder.from;
        size = builder.size;
        versionService = builder.versionService;
    }

    public FindNodeVersionsResult execute()
    {
        final GetVersionsQuery query = GetVersionsQuery.create().
            nodeId( this.nodeId ).
            from( this.from ).
            size( this.size ).
            build();

        return this.versionService.findVersions( query, ContextAccessor.current().getRepositoryId() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private int from;

        private int size;

        private VersionService versionService;

        private Builder()
        {
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder from( int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( int size )
        {
            this.size = size;
            return this;
        }

        public Builder versionService( VersionService versionService )
        {
            this.versionService = versionService;
            return this;
        }

        public GetNodeVersionsCommand build()
        {
            return new GetNodeVersionsCommand( this );
        }
    }
}
