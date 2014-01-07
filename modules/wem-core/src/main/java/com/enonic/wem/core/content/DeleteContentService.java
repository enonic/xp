package com.enonic.wem.core.content;


import com.enonic.wem.api.command.content.DeleteContent;
import com.enonic.wem.api.command.entity.DeleteNodeByPath;
import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.DeleteContentResult;
import com.enonic.wem.api.content.UnableToDeleteContentException;
import com.enonic.wem.api.entity.NoNodeAtPathFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.entity.DeleteNodeByPathService;
import com.enonic.wem.core.entity.GetNodeByPathService;
import com.enonic.wem.core.index.IndexService;


public class DeleteContentService
    extends ContentService
{
    private final DeleteContent command;

    private final IndexService indexService;

    public DeleteContentService( final CommandContext context, final DeleteContent command, final IndexService indexService )
    {
        super( context );
        this.command = command;
        this.indexService = indexService;
    }

    public DeleteContentResult execute()
        throws Exception
    {
        final DeleteNodeByPath deleteNodeByPathCommand =
            new DeleteNodeByPath( ContentNodeHelper.translateContentPathToNodePath( command.getContentPath() ) );

        try
        {
            final Node nodeToDelete =
                new GetNodeByPathService( this.session, new GetNodeByPath( deleteNodeByPathCommand.getPath() ) ).execute();
            final Content contentToDelete = translator.fromNode( nodeToDelete );

            if ( new ChildContentIdsResolver( this.context ).resolve( contentToDelete ).hasChildren() )
            {
                throw new UnableToDeleteContentException( command.getContentPath(), "Content has children" );
            }

            final Node deletedNode = new DeleteNodeByPathService( this.session, this.indexService, deleteNodeByPathCommand ).execute();

            final Content deletedContent = translator.fromNode( deletedNode );

            return new DeleteContentResult( deletedContent );
        }
        catch ( NoNodeAtPathFoundException e )
        {
            throw new ContentNotFoundException( command.getContentPath() );
        }
    }
}
