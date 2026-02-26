package com.enonic.xp.repo.impl.node;

import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.security.acl.AccessControlList;

final class ApplyPermissionsResult
{
    private final Map<NodeId, List<BranchResult>> results;

    private ApplyPermissionsResult( Builder builder )
    {
        this.results = (ImmutableMap) builder.results.build().asMap();
    }

    static Builder create()
    {
        return new Builder();
    }

    Map<NodeId, List<BranchResult>> getResults()
    {
        return results;
    }

    record BranchResult(Branch branch, @Nullable NodeVersion nodeVersion, @Nullable AccessControlList permissions)
    {
    }

    static final class Builder
    {
        private final ImmutableListMultimap.Builder<NodeId, BranchResult> results = ImmutableListMultimap.builder();

        private Builder()
        {
        }

        Builder addResult( NodeId nodeId, Branch branch, @Nullable NodeVersion nodeVersion, @Nullable AccessControlList permissions )
        {
            results.put( nodeId, new BranchResult( branch, nodeVersion, permissions ) );
            return this;
        }

        ApplyPermissionsResult build()
        {
            return new ApplyPermissionsResult( this );
        }
    }
}
