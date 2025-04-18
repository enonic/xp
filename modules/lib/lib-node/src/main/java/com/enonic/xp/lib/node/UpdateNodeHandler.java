package com.enonic.xp.lib.node;

import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.script.ScriptValue;

public class UpdateNodeHandler
    extends AbstractNodeHandler
{
    private NodeKey key;

    private ScriptValue editor;

    private UpdateNodeHandler( final Builder builder )
    {
        super( builder );
        this.key = builder.key;
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

        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create()
            .id( node.id() ).editor( editorInput.editor() ).setBinaryAttachments( editorInput.binaryAttachments() )
            .build();

        final Node updatedNode = this.nodeService.update( updateNodeParams );
        return new NodeMapper( updatedNode, false );
    }

    private Node getExistingNode()
    {
        final Node node = doGetNode( key );

        if ( node == null )
        {
            throw new NodeNotFoundException( "Cannot update node with key: [" + this.key.getValue() + "]" );
        }

        return node;
    }

    public void setKey( final String key )
    {
        this.key = NodeKey.from( key );
    }

    @SuppressWarnings("unused")
    public void setEditor( final ScriptValue editor )
    {
        this.editor = editor;
    }


    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey key;

        private ScriptValue editor;

        private Builder()
        {
        }

        public Builder key( final NodeKey val )
        {
            key = val;
            return this;
        }

        public Builder editor( final ScriptValue val )
        {
            editor = val;
            return this;
        }

        public UpdateNodeHandler build()
        {
            return new UpdateNodeHandler( this );
        }
    }
}
