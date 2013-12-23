package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.api.command.schema.content.GetContentType;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNotFoundException;
import com.enonic.wem.core.entity.GetNodeByPathService;

import static com.enonic.wem.api.entity.NodePath.newPath;

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
            Commands.node().get().byPath( newPath( "/content-types/" + contentTypeName.toString() ).build() );

        try
        {
            final Node contentTypeNode = new GetNodeByPathService( this.context.getJcrSession(), getNodeByPathCommand ).execute();
            final ContentTypeInheritorResolver contentTypeInheritorResolver = new ContentTypeInheritorResolver( this.context.getClient() );
            return nodeToContentType( contentTypeNode, contentTypeInheritorResolver );

        }
        catch ( NoNodeAtPathFound e )
        {
            if ( command.isNotFoundAsException() )
            {
                throw new ContentTypeNotFoundException( command.getContentTypeName() );
            }
            else
            {
                return null;
            }
        }
    }
}
