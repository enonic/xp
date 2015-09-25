package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistException;
import com.enonic.xp.content.ContentChangeEvent;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.RenameNodeParams;

import static com.enonic.xp.core.impl.content.ContentNodeHelper.translateNodePathToContentPath;


final class RenameContentCommand
    extends AbstractContentCommand
{
    private final RenameContentParams params;

    private RenameContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    Content execute()
    {
        params.validate();

        try
        {
            return doExecute();
        }
        catch ( final NodeAlreadyExistAtPathException e )
        {
            final ContentPath path = translateNodePathToContentPath( e.getNode() );
            throw new ContentAlreadyExistException( path );
        }
    }

    private Content doExecute()
    {
        final NodeId nodeId = NodeId.from( params.getContentId() );
        final Node existingNode = nodeService.getById( nodeId );

        final NodeName nodeName = NodeName.from( params.getNewName().toString() );
        nodeService.rename( RenameNodeParams.create().
            nodeId( nodeId ).
            nodeName( nodeName ).
            build() );

        final Content content = getContent( params.getContentId() );

        final ContentChangeEvent event = ContentChangeEvent.create().
            change( ContentChangeEvent.ContentChangeType.RENAME, translateNodePathToContentPath( existingNode.path() ) ).
            change( ContentChangeEvent.ContentChangeType.DELETE, translateNodePathToContentPath( existingNode.path() ) ).
            change( ContentChangeEvent.ContentChangeType.CREATE, content.getPath() ).
            build();
        eventPublisher.publish( event );

        this.nodeService.refresh();

        return content;
    }

    public static Builder create( final RenameContentParams params )
    {
        return new Builder( params );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final RenameContentParams params;

        public Builder( final RenameContentParams params )
        {
            this.params = params;
        }

        @Override
        void validate()
        {
            super.validate();
        }

        public RenameContentCommand build()
        {
            validate();
            return new RenameContentCommand( this );
        }

    }


}

