package com.enonic.xp.elasticsearch.impl.status.cluster;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

final class ClusterState
{
    private final String clusterName;

    private final String errorMessage;

    private final LocalNodeState localNodeState;

    private final List<MemberNodeState> memberNodeStateList;

    String getClusterName()
    {
        return clusterName;
    }

    String getErrorMessage()
    {
        return errorMessage;
    }

    LocalNodeState getLocalNodeState()
    {
        return localNodeState;
    }

    List<MemberNodeState> getMemberNodeStateList()
    {
        return memberNodeStateList;
    }

    private ClusterState( Builder builder )
    {
        this.clusterName = builder.clusterName;
        this.errorMessage = builder.errorMessage;
        this.localNodeState = builder.localNodeState;
        this.memberNodeStateList = builder.memberNodeStateList;
    }


    static ClusterState.Builder create()
    {
        return new Builder();
    }

    static class Builder
    {

        private String clusterName;

        private String errorMessage;

        private LocalNodeState localNodeState;

        private final List<MemberNodeState> memberNodeStateList = Lists.newArrayList();

        private Builder()
        {
        }

        Builder clusterName( final String clusterName )
        {
            this.clusterName = clusterName;
            return this;
        }

        Builder errorMessage( final String errorMessage )
        {
            this.errorMessage = errorMessage;
            return this;
        }

        Builder localNodeState( final LocalNodeState localNodeState )
        {
            this.localNodeState = localNodeState;
            return this;
        }

        Builder addMemberNodeStates( final Collection<MemberNodeState> memberNodeStates )
        {
            this.memberNodeStateList.addAll( memberNodeStates );
            return this;
        }

        Builder addMemberNodeState( final MemberNodeState memberNodeState )
        {
            this.memberNodeStateList.add( memberNodeState );
            return this;
        }

        ClusterState build()
        {
            return new ClusterState( this );
        }
    }


}
