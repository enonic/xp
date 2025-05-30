package com.enonic.xp.node;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.util.BinaryReference;

@PublicApi
public final class UpdateNodeParams
{
    private final NodeId id;

    private final NodePath path;

    private final NodeEditor editor;

    private final BinaryAttachments binaryAttachments;

    private final RefreshMode refresh;

    private UpdateNodeParams( final Builder builder )
    {
        this.id = builder.id;
        this.path = builder.path;
        this.editor = builder.editor;
        this.binaryAttachments = builder.binaryAttachments.build();
        this.refresh = builder.refresh;
    }

    public BinaryAttachments getBinaryAttachments()
    {
        return binaryAttachments;
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

    public RefreshMode getRefresh()
    {
        return refresh;
    }

    public static final class Builder
    {
        private NodeId id;

        private NodePath path;

        private NodeEditor editor;

        private BinaryAttachments.Builder binaryAttachments = BinaryAttachments.create();

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

        public Builder refresh( final RefreshMode refresh )
        {
            this.refresh = refresh;
            return this;
        }

        public UpdateNodeParams build()
        {
            this.validate();
            return new UpdateNodeParams( this );
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
