package com.enonic.xp.repo.impl.entity;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.repo.impl.version.VersionService;

public class FindNodesWithVersionDifferenceCommand
{
    private final NodeVersionDiffQuery query;

    private final VersionService versionService;

    private FindNodesWithVersionDifferenceCommand( Builder builder )
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

        public FindNodesWithVersionDifferenceCommand build()
        {
            return new FindNodesWithVersionDifferenceCommand( this );
        }
    }
}