package com.enonic.wem.core.schema.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.DeleteNodeByPath;
import com.enonic.wem.api.command.schema.content.DeleteContentType;
import com.enonic.wem.api.command.schema.content.DeleteContentTypeResult;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.UnableToDeleteContentTypeException;
import com.enonic.wem.core.entity.DeleteNodeByPathService;
import com.enonic.wem.core.index.IndexService;


public final class DeleteContentTypeHandler
    extends AbstractContentTypeHandler<DeleteContentType>
{
    private IndexService indexService;

    @Override
    public void handle()
        throws Exception
    {
        final DeleteNodeByPath deleteNodeByPathCommand =
            Commands.node().delete().byPath( new NodePath( "/content-types/" + command.getName().toString() ) );

        final Node deletedNode = new DeleteNodeByPathService( context.getJcrSession(), indexService, deleteNodeByPathCommand ).execute();

        final ContentTypeInheritorResolver contentTypeInheritorResolver = new ContentTypeInheritorResolver( this.context.getClient() );
        final ContentTypeNames inheritors = contentTypeInheritorResolver.resolveInheritors( command.getName() );
        if ( inheritors.isNotEmpty() )
        {
            throw new UnableToDeleteContentTypeException( command.getName(), "Inheritors found: " + inheritors.toString() );
        }

        final ContentType deletedContentType = this.nodeToContentType( deletedNode, contentTypeInheritorResolver );
        command.setResult( new DeleteContentTypeResult( deletedContentType ) );
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
