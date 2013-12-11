package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.api.command.schema.content.GetContentType;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.entity.GetNodeByPathHandler;

public class GetContentTypeHandler
    extends AbstractContentTypeHandler<GetContentType>
{
    @Override
    public void handle()
        throws Exception
    {
        final ContentType contentType = getContentType( command.getContentTypeName() );

        if ( contentType == null )
        {
            command.setResult( null );
        }
        else if ( !command.isMixinReferencesToFormItems() )
        {
            command.setResult( contentType );
        }
        else
        {
            command.setResult( transformMixinReferences( contentType ) );
        }
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {

        final GetNodeByPath getNodeByPathCommand =
            Commands.node().get().byPath( NodePath.newPath( "/content-types/" + contentTypeName.toString() ).build() );

        final Node contentTypeNode = getNodeByPath( getNodeByPathCommand );

        if ( contentTypeNode == null )
        {
            return null;
        }

        final ContentTypeInheritorResolver contentTypeInheritorResolver = new ContentTypeInheritorResolver( this.context.getClient() );
        return nodeToContentType( contentTypeNode, contentTypeInheritorResolver );
    }

    private Node getNodeByPath( final GetNodeByPath getNodeByPathCommand )
    {
        final GetNodeByPathHandler getNodeByPathHandler =
            GetNodeByPathHandler.create().command( getNodeByPathCommand ).context( this.context ).build();

        getNodeByPathHandler.handle();

        return getNodeByPathCommand.getResult();
    }
}
