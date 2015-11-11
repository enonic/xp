package com.enonic.xp.elasticsearch.impl.status;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

public final class ClusterState
{
    private String clusterName;

    private String errorMessage;

    private LocalNodeState localNodeState;

    private List<MemberNodeState> memberNodeStateList;

    public String getClusterName()
    {
        return clusterName;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public LocalNodeState getLocalNodeState()
    {
        return localNodeState;
    }

    public List<MemberNodeState> getMemberNodeStateList()
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


    public static ClusterState.Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {

        private String clusterName;

        private String errorMessage;

        private LocalNodeState localNodeState;

        private List<MemberNodeState> memberNodeStateList = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder clusterName( final String clusterName )
        {
            this.clusterName = clusterName;
            return this;
        }

        public Builder errorMessage( final String errorMessage )
        {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder localNodeState( final LocalNodeState localNodeState )
        {
            this.localNodeState = localNodeState;
            return this;
        }

        public Builder addMemberNodeStates( final Collection<MemberNodeState> memberNodeStates )
        {
            this.memberNodeStateList.addAll( memberNodeStates );
            return this;
        }

        public Builder addMemberNodeState( final MemberNodeState memberNodeState )
        {
            this.memberNodeStateList.add( memberNodeState );
            return this;
        }

        public ClusterState build()
        {
            return new ClusterState( this );
        }
    }


}
