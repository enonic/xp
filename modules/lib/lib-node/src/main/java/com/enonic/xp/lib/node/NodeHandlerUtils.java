package com.enonic.xp.lib.node;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.lib.value.ScriptValueTranslator;
import com.enonic.xp.lib.value.ScriptValueTranslatorResult;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeEditor;
import com.enonic.xp.script.ScriptValue;

final class NodeHandlerUtils
{
    private NodeHandlerUtils()
    {
    }

    static NodeEditorInput prepareEditorInput( final Node node, final ScriptValue editorScript )
    {
        final ScriptValue appliedResult = applyEditor( node, editorScript );

        final BinaryAttachments attachments = new BinaryAttachmentsParser().parse( appliedResult );
        final NodeEditor nodeEditor = createEditor( appliedResult );

        return new NodeEditorInput( attachments, nodeEditor );
    }

    private static ScriptValue applyEditor( final Node node, final ScriptValue editor )
    {
        final NodeMapper nodeMapper = new NodeMapper( node, true );
        return editor.call( nodeMapper );
    }

    private static NodeEditor createEditor( final ScriptValue updatedNode )
    {
        return edit -> {
            if ( updatedNode != null )
            {
                final ScriptValueTranslatorResult result = new ScriptValueTranslator( false ).create( updatedNode );
                final PropertyTree tree = result.getPropertyTree();
                UpdateNodeExecutor.create().editableNode( edit ).propertyTree( tree ).build().execute();
            }
        };
    }
}
