package com.enonic.xp.node;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.security.acl.AccessControlList;

@PublicApi
public final class ApplyNodePermissionsResult
{
    private final Map<NodeId, List<BranchResult>> results;


    private ApplyNodePermissionsResult( Builder builder )
    {
        this.results = (ImmutableMap) builder.results.build().asMap();

    }

    public static Builder create()
    {
        return new Builder();
    }

    public Map<NodeId, List<BranchResult>> getResults()
    {
        return results;
    }

    public AccessControlList getResult( final NodeId nodeId, final Branch branch )
    {
        final List<BranchResult> results = this.results.get( nodeId );

        return results != null ? this.results.get( nodeId )
            .stream()
            .filter( br -> br.branch.equals( branch ) )
            .map( BranchResult::permissions )
            .filter( Objects::nonNull )
            .findAny()
            .orElse( null ) : null;
    }

    public record BranchResult(Branch branch, NodeVersionId nodeVersionId, AccessControlList permissions)
    {

    }

    public static final class Builder
    {
        private final ImmutableListMultimap.Builder<NodeId, BranchResult> results = ImmutableListMultimap.builder();

        private Builder()
        {
        }

        public Builder addResult( NodeId nodeId, Branch branch, NodeVersionId nodeVersionId, AccessControlList permissions )
        {
            results.put( nodeId, new BranchResult( branch, nodeVersionId, permissions ) );
            return this;
        }

        public ApplyNodePermissionsResult build()
        {
            return new ApplyNodePermissionsResult( this );
        }
    }
}
