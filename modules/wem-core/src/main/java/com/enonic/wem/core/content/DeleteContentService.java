package com.enonic.wem.core.content;


import com.enonic.wem.api.command.content.DeleteContent;
import com.enonic.wem.api.command.schema.content.ContentTypeService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.DeleteContentResult;
import com.enonic.wem.api.content.UnableToDeleteContentException;
import com.enonic.wem.api.entity.DeleteNodeByPathParams;
import com.enonic.wem.api.entity.GetNodeByPathParams;
import com.enonic.wem.api.entity.NoNodeAtPathFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.core.command.CommandContext;


public class DeleteContentService
    extends ContentService
{
    private final DeleteContent command;

    public DeleteContentService( final CommandContext context,
                                 final DeleteContent command,
                                 final NodeService nodeService,
                                 final ContentTypeService contentTypeService )
    {
        super( context, nodeService, contentTypeService );
        this.command = command;
    }

    public DeleteContentResult execute()
        throws Exception
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( command.getContentPath() );

        try
        {
            final Node nodeToDelete = nodeService.getByPath( new GetNodeByPathParams( nodePath ) );
            final Content contentToDelete = translator.fromNode( nodeToDelete );

            if ( new ChildContentIdsResolver( this.context, this.nodeService, this.contentTypeService ).resolve( contentToDelete ).hasChildren() )
            {
                throw new UnableToDeleteContentException( command.getContentPath(), "Content has children" );
            }

            final Node deletedNode = nodeService.deleteByPath( new DeleteNodeByPathParams( nodePath ) );

            final Content deletedContent = translator.fromNode( deletedNode );

            return new DeleteContentResult( deletedContent );
        }
        catch ( NoNodeAtPathFoundException e )
        {
            throw new ContentNotFoundException( command.getContentPath() );
        }
    }
}
