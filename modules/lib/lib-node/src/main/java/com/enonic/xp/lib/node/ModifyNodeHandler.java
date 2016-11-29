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

public class ModifyNodeHandler
    extends BaseNodeHandler
{
    private NodeKey key;

    private ScriptValue editor;

    private ModifyNodeHandler( final Builder builder )
    {
        super( builder );
        key = builder.key;
        setEditor( builder.editor );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Object execute()
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

        if ( this.key.isId() )
        {
            node = this.nodeService.getById( key.getAsNodeId() );
        }
        else
        {
            node = this.nodeService.getByPath( key.getAsPath() );
        }

        if ( node == null )
        {
            throw new NodeNotFoundException( "Cannot modify node with key: [" + this.key.getValue() + "]" );
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
        extends BaseNodeHandler.Builder<Builder>
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

        public ModifyNodeHandler build()
        {
            return new ModifyNodeHandler( this );
        }
    }
}
