package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.node.NodeVersionDiffQuery;
import com.enonic.wem.api.node.NodeVersionDiffResult;
import com.enonic.wem.repo.internal.version.VersionService;

public class NodeVersionDiffCommand
{
    private final NodeVersionDiffQuery query;

    private final VersionService versionService;

    private NodeVersionDiffCommand( Builder builder )
    {
        query = builder.query;
        versionService = builder.versionService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersionDiffResult execute()
    {
        return this.versionService.diff( query, ContextAccessor.current().getRepositoryId() );
    }

    public static final class Builder
    {
        private NodeVersionDiffQuery query;

        private VersionService versionService;

        private Builder()
        {
        }

        public Builder query( NodeVersionDiffQuery query )
        {
            this.query = query;
            return this;
        }

        public Builder versionService( VersionService versionService )
        {
            this.versionService = versionService;
            return this;
        }

        public NodeVersionDiffCommand build()
        {
            return new NodeVersionDiffCommand( this );
        }
    }
}