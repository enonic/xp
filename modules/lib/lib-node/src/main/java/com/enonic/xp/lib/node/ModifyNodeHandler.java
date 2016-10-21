package com.enonic.xp.lib.node;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeEditor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.script.ScriptValue;

public class ModifyNodeHandler
    extends BaseNodeHandler
{
    private Key key;

    private ScriptValue editor;

    @Override
    protected Object doExecute()
    {
        final Node node = getExistingNode();

        final ScriptValue value = this.editor.call( new NodeMapper( node ) );
        final BinaryAttachments binaryAttachments = new BinaryAttachmentsParser().parse( value );

        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            id( node.id() ).
            editor( createEditor() ).
            setBinaryAttachments( binaryAttachments ).
            build();

        final Node updatedNode = this.nodeService.update( updateNodeParams );

        return new NodeMapper( updatedNode );
    }

    private NodeEditor createEditor()
    {
        return edit -> {
            final ScriptValue value = this.editor.call( new NodeMapper( edit.source ) );
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

        ModifyNodeExecutor.create().
            editableNode( target ).
            propertyTree( nodeAsPropertyTree ).
            build().
            execute();
    }

    private Node getExistingNode()
    {
        final Node node;

        if ( this.key.isId )
        {
            node = this.nodeService.getById( key.getAsNodeId() );
        }
        else
        {
            node = this.nodeService.getByPath( key.getAsPath() );
        }

        if ( node == null )
        {
            throw new NodeNotFoundException( "Cannot modify node with key: [" + this.key.value + "]" );
        }
        return node;
    }

    public void setKey( final String key )
    {
        this.key = new Key( key );
    }

    @SuppressWarnings("unused")
    public void setEditor( final ScriptValue editor )
    {
        this.editor = editor;
    }

    private static class Key
    {
        private final boolean isId;

        private final String value;

        public Key( final String value )
        {
            this.value = value;
            this.isId = !value.startsWith( "/" );
        }

        NodeId getAsNodeId()
        {
            return NodeId.from( this.value );
        }

        NodePath getAsPath()
        {
            return NodePath.create( this.value ).build();
        }
    }


}
