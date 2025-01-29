package com.enonic.xp.node;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;

@PublicApi
public final class PatchNodeParams
{
    private final NodeId id;

    private final NodeEditor editor;

    private final Branches branches;


    private PatchNodeParams( final Builder builder )
    {
        this.id = builder.id;
        this.editor = builder.editor;
        branches = Branches.from( builder.branches.build() );

    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId getId()
    {
        return id;
    }

    public NodeEditor getEditor()
    {
        return editor;
    }

    public Branches getBranches()
    {
        return branches;
    }


    public static final class Builder
    {
        private final ImmutableSet.Builder<Branch> branches = ImmutableSet.builder();

        private NodeId id;

        private NodeEditor editor;


        private Builder()
        {
        }

        public Builder id( final NodeId id )
        {
            this.id = id;
            return this;
        }

        public Builder editor( final NodeEditor editor )
        {
            this.editor = editor;
            return this;
        }

        public Builder addBranches( final Branches branches )
        {
            this.branches.addAll( branches );
            return this;
        }

        public PatchNodeParams build()
        {
            this.validate();
            return new PatchNodeParams( this );
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.id, "id cannot be null" );
            Preconditions.checkNotNull( this.editor, "editor cannot be null" );
        }
    }
}
