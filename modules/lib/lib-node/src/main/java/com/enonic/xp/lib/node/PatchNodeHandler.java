package com.enonic.xp.lib.node;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.lib.node.mapper.PatchNodeResultMapper;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;
import com.enonic.xp.script.ScriptValue;

@SuppressWarnings("unused")
public final class PatchNodeHandler
    extends AbstractNodeHandler
{
    private final NodeKey nodeKey;

    private final Branches branches;

    private final ScriptValue editor;

    private PatchNodeHandler( final Builder builder )
    {
        super( builder );
        this.nodeKey = builder.key;
        this.branches = builder.branches;
        this.editor = builder.editor;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Object execute()
    {
        final Node node = getExistingNode();
        final NodeEditorInput editorInput = NodeHandlerUtils.prepareEditorInput( node, this.editor );

        final PatchNodeParams params = PatchNodeParams.create()
            .id( getNodeId( this.nodeKey ) )
            .branches( this.branches )
            .editor( editorInput.editor() )
            .setBinaryAttachments( editorInput.binaryAttachments() )
            .build();

        final PatchNodeResult result = this.nodeService.patch( params );

        return new PatchNodeResultMapper( result );
    }

    private Node getExistingNode()
    {
        final Node node = doGetNode( nodeKey );

        if ( node == null )
        {
            throw new NodeNotFoundException( "Cannot find node with key: [" + this.nodeKey.getValue() + "]" );
        }

        return node;
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey key;

        private Branches branches;

        private ScriptValue editor;

        private Builder()
        {
        }

        public Builder key( final NodeKey key )
        {
            this.key = key;
            return this;
        }

        public Builder branches( final Branches branches )
        {
            this.branches = branches;
            return this;
        }

        public Builder editor( final ScriptValue editor )
        {
            this.editor = editor;
            return this;
        }

        public PatchNodeHandler build()
        {
            return new PatchNodeHandler( this );
        }
    }
}
