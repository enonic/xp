package com.enonic.xp.node;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.util.BinaryReference;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;


public final class PatchNodeParams
{
    private final NodeId id;

    private final NodePath path;

    private final NodeEditor editor;

    private final BinaryAttachments binaryAttachments;

    private final VersionAttributesResolver versionAttributesResolver;

    private final RefreshMode refresh;

    private final Branches branches;

    private PatchNodeParams( final Builder builder )
    {
        this.id = builder.id;
        this.path = builder.path;
        this.editor = builder.editor;
        this.binaryAttachments = builder.binaryAttachments.build();
        this.versionAttributesResolver = builder.versionAttributesResolver;
        this.refresh = builder.refresh;
        this.branches = requireNonNullElse( builder.branches, Branches.empty() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public BinaryAttachments getBinaryAttachments()
    {
        return binaryAttachments;
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

    public RefreshMode getRefresh()
    {
        return refresh;
    }

    public Branches getBranches()
    {
        return branches;
    }

    public VersionAttributesResolver getVersionAttributesResolver()
    {
        return versionAttributesResolver;
    }

    public static final class Builder
    {
        private Branches branches;

        private NodeId id;

        private NodePath path;

        private NodeEditor editor;

        private BinaryAttachments.Builder binaryAttachments = BinaryAttachments.create();

        private VersionAttributesResolver versionAttributesResolver;

        private RefreshMode refresh;

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

        public Builder attachBinary( final BinaryReference binaryReference, final ByteSource byteSource )
        {
            this.binaryAttachments.add( new BinaryAttachment( binaryReference, byteSource ) );
            return this;
        }

        public Builder setBinaryAttachments( final BinaryAttachments binaryAttachments )
        {
            this.binaryAttachments = BinaryAttachments.create();
            if ( binaryAttachments != null )
            {
                binaryAttachments.stream().forEach( this.binaryAttachments::add );
            }
            return this;
        }

        public Builder versionAttributesResolver( final VersionAttributesResolver versionAttributesResolver )
        {
            this.versionAttributesResolver = versionAttributesResolver;
            return this;
        }

        public Builder refresh( final RefreshMode refresh )
        {
            this.refresh = refresh;
            return this;
        }

        public Builder branches( final Branches branches )
        {
            this.branches = branches;
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
                throw new IllegalArgumentException( "Either id or path is required" );
            }
            requireNonNull( this.editor, "editor is required" );
        }
    }
}
