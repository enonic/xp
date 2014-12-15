package com.enonic.wem.api.node;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class UpdateNodeParams
{
    private final NodeId id;

    private final NodeEditor editor;

    private final BinaryAttachments binaryAttachments;

    private UpdateNodeParams( final Builder builder )
    {
        id = builder.id;
        editor = builder.editor;
        binaryAttachments = new BinaryAttachments( ImmutableSet.copyOf( builder.binaryAttachments ) );
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

    public static final class Builder
    {
        private NodeId id;

        private NodeEditor editor;

        private Set<BinaryAttachment> binaryAttachments = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder id( NodeId id )
        {
            this.id = id;
            return this;
        }

        public Builder editor( NodeEditor editor )
        {
            this.editor = editor;
            return this;
        }

        public Builder attachBinary( final BinaryAttachment binaryAttachment )
        {
            this.binaryAttachments.add( binaryAttachment );
            return this;
        }

        public UpdateNodeParams build()
        {
            this.validate();
            return new UpdateNodeParams( this );
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.id, "id cannot be null" );
            Preconditions.checkNotNull( this.editor, "editor cannot be null" );
        }
    }
}
