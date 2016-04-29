package com.enonic.xp.core.impl.content;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.UpdateContentTranslatorParams;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.node.NodeEditor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.UpdateNodeParams;

public class UpdateNodeParamsFactory
{
    private static final ContentDataSerializer CONTENT_DATA_SERIALIZER = new ContentDataSerializer();

    public static UpdateNodeParams create( final UpdateContentTranslatorParams params )
    {
        final Content editedContent = params.getEditedContent();
        final CreateAttachments createAttachments = params.getCreateAttachments();

        final NodeEditor nodeEditor = toNodeEditor( params );

        final UpdateNodeParams.Builder builder = UpdateNodeParams.create().
            id( NodeId.from( editedContent.getId() ) ).
            editor( nodeEditor );

        if ( createAttachments != null )
        {
            for ( final CreateAttachment createAttachment : createAttachments )
            {
                builder.attachBinary( createAttachment.getBinaryReference(), createAttachment.getByteSource() );
            }
        }
        return builder.build();
    }

    private static NodeEditor toNodeEditor( final UpdateContentTranslatorParams params )
    {
        final Content content = params.getEditedContent();

        final PropertyTree nodeData = CONTENT_DATA_SERIALIZER.toUpdateNodeData( params );

        final IndexConfigDocument indexConfigDocument = ContentIndexConfigFactory.create( content );

        return editableNode -> {
            editableNode.name = NodeName.from( content.getName().toString() );
            editableNode.indexConfigDocument = indexConfigDocument;
            editableNode.data = nodeData;
            editableNode.permissions = content.getPermissions();
            editableNode.inheritPermissions = content.inheritsPermissions();
        };
    }
}


