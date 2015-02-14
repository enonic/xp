package com.enonic.xp.core.impl.content;

import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.ContentAlreadyExistException;
import com.enonic.xp.core.content.ContentChangeEvent;
import com.enonic.xp.core.content.ContentPath;
import com.enonic.xp.core.content.RenameContentParams;
import com.enonic.xp.core.node.Node;
import com.enonic.xp.core.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.core.node.NodeId;
import com.enonic.xp.core.node.NodeName;
import com.enonic.xp.core.node.RenameNodeParams;

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
            change( ContentChangeEvent.ContentChangeType.DELETE, translateNodePathToContentPath( existingNode.path() ) ).
            change( ContentChangeEvent.ContentChangeType.CREATE, content.getPath() ).
            build();
        eventPublisher.publish( event );

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

