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

    private final NodePath path;

    private final NodeEditor editor;

    private final Branches branches;


    private PatchNodeParams( final Builder builder )
    {
        this.id = builder.id;
        this.path = builder.path;
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

    public NodePath getPath()
    {
        return path;
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

        private NodePath path;

        private NodeEditor editor;


        private Builder()
        {
        }

        public Builder id( final NodeId id )
        {
            this.id = id;
            return this;
        }

        public Builder path( final NodePath path )
        {
            this.path = path;
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
            if ( this.id == null && this.path == null )
            {
                throw new NullPointerException( "id and path cannot be both null" );
            }
            Preconditions.checkNotNull( this.editor, "editor cannot be null" );
        }
    }
}
