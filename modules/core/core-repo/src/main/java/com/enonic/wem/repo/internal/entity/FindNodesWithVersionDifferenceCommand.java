package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.version.VersionService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;

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
        return this.versionService.diff( query, InternalContext.from( ContextAccessor.current() ) );
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