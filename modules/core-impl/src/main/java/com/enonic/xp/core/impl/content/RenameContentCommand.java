package com.enonic.xp.core.impl.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentAlreadyExistException;
import com.enonic.wem.api.content.ContentChangeEvent;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.RenameContentParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeAlreadyExistAtPathException;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.RenameNodeParams;

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

