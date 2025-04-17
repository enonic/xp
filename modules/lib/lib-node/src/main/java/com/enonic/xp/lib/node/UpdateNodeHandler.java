package com.enonic.xp.lib.node;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.lib.value.ScriptValueTranslator;
import com.enonic.xp.lib.value.ScriptValueTranslatorResult;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeEditor;
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
        key = builder.key;
        setEditor( builder.editor );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Object execute()
    {
        final Node node = getExistingNode();
        final ScriptValue updatedNodeScriptValue = applyEditor( node );
        final BinaryAttachments binaryAttachments = getBinaryAttachments( updatedNodeScriptValue );

        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create()
            .id( node.id() )
            .editor( createEditor( updatedNodeScriptValue ) )
            .setBinaryAttachments( binaryAttachments )
            .build();

        final Node updatedNode = this.nodeService.update( updateNodeParams );
        return new NodeMapper( updatedNode, false );
    }

    private ScriptValue applyEditor( final Node node )
    {
        final NodeMapper nodeMapper = new NodeMapper( node, true );
        return this.editor.call( nodeMapper );
    }

    private BinaryAttachments getBinaryAttachments( final ScriptValue node )
    {
        return new BinaryAttachmentsParser().parse( node );
    }

    private NodeEditor createEditor( final ScriptValue updatedNode )
    {
        return edit -> {
            final ScriptValue value = updatedNode;
            if ( value != null )
            {
                updateNode( edit, value );
            }
        };
    }

    private void updateNode( final EditableNode target, final ScriptValue scriptValue )
    {
        final ScriptValueTranslatorResult scriptValueTranslatorResult = new ScriptValueTranslator( false ).create( scriptValue );
        final PropertyTree nodeAsPropertyTree = scriptValueTranslatorResult.getPropertyTree();

        UpdateNodeExecutor.create().editableNode( target ).propertyTree( nodeAsPropertyTree ).build().execute();
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
