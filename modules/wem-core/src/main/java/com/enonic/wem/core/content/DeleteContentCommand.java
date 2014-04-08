package com.enonic.wem.core.content;


import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.DeleteContentParams;
import com.enonic.wem.api.content.DeleteContentResult;
import com.enonic.wem.api.content.UnableToDeleteContentException;
import com.enonic.wem.api.entity.NoNodeAtPathFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;


final class DeleteContentCommand
    extends AbstractContentCommand<DeleteContentCommand>
{
    private DeleteContentParams params;

    DeleteContentResult execute()
    {
        params.validate();

        return doExecute();
    }

    private DeleteContentResult doExecute()
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( this.params.getContentPath() );

        try
        {
            final Node nodeToDelete = nodeService.getByPath( nodePath );
            final Content contentToDelete = getTranslator().fromNode( nodeToDelete );

            if ( new ChildContentIdsResolver(
                this.nodeService, this.contentTypeService, this.blobService ).resolve( contentToDelete ).hasChildren() )
            {
                throw new UnableToDeleteContentException( this.params.getContentPath(), "Content has children" );
            }

            final Node deletedNode = nodeService.deleteByPath( nodePath );

            final Content deletedContent = getTranslator().fromNode( deletedNode );

            return new DeleteContentResult( deletedContent );
        }
        catch ( NoNodeAtPathFoundException e )
        {
            throw new ContentNotFoundException( this.params.getContentPath() );
        }
    }

    DeleteContentCommand params( final DeleteContentParams params )
    {
        this.params = params;
        return this;
    }
}
