package com.enonic.xp.core.impl.content;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.node.CreateNodeParams;

public class CreateNodeParamsFactory
{
    private static final ContentDataSerializer CONTENT_DATA_SERIALIZER = new ContentDataSerializer();

    public static CreateNodeParams create( final CreateContentTranslatorParams params )
    {
        final PropertyTree contentAsData = CONTENT_DATA_SERIALIZER.toCreateNodeData( params );

        final IndexConfigDocument indexConfigDocument = ContentIndexConfigFactory.create( params );

        final CreateNodeParams.Builder builder = CreateNodeParams.create().
            name( resolveNodeName( params.getName() ) ).
            parent( ContentNodeHelper.translateContentParentToNodeParentPath( params.getParent() ) ).
            data( contentAsData ).
            indexConfigDocument( indexConfigDocument ).
            permissions( params.getPermissions() ).
            inheritPermissions( params.isInheritPermissions() ).
            childOrder( params.getChildOrder() ).
            nodeType( ContentConstants.CONTENT_NODE_COLLECTION );

        for ( final CreateAttachment attachment : params.getCreateAttachments() )
        {
            builder.attachBinary( attachment.getBinaryReference(), attachment.getByteSource() );
        }

        return builder.build();
    }

    private static String resolveNodeName( final ContentName name )
    {
        if ( name.isUnnamed() && !name.hasUniqueness() )
        {
            return ContentName.uniqueUnnamed().toString();
        }

        return name.toString();
    }

}
