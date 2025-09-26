package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentAlreadyMovedException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.MoveContentException;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;

final class MoveContentCommand
    extends AbstractContentCommand
{
    private final MoveContentParams params;

    private MoveContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create( final MoveContentParams params )
    {
        return new Builder( params );
    }

    MoveContentsResult execute()
    {
        try
        {
            return doExecute();
        }
        catch ( MoveNodeException e )
        {
            throw new MoveContentException( e.getMessage(), ContentPath.from( e.getPath().toString() ) );
        }
        catch ( NodeAlreadyExistAtPathException e )
        {
            throw new ContentAlreadyExistsException( ContentPath.from( e.getNode().toString() ), e.getRepositoryId(), e.getBranch() );
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }
    }

    private MoveContentsResult doExecute()
    {
        final ContentId contentId = params.getContentId();
        final Content sourceContent = getContent( contentId );

        final NodePath newParentPath = ContentNodeHelper.translateContentPathToNodePath( params.getParentContentPath() );

        if ( sourceContent.getParentPath().equals( params.getParentContentPath() ) )
        {
            throw new ContentAlreadyMovedException(
                String.format( "Content with name [%s] is already a child of [%s]", sourceContent.getName(), params.getParentContentPath() ),
                sourceContent.getPath() );
        }

        validateParentChildRelations( params.getParentContentPath(), sourceContent.getType() );

        final NodeId sourceNodeId = NodeId.from( contentId );

        final MoveNodeParams.Builder moveParams = MoveNodeParams.create()
            .nodeId( sourceNodeId )
            .newParentPath( newParentPath )
            .refresh( RefreshMode.ALL );

        if ( params.getMoveContentListener() != null )
        {
            moveParams.moveListener( this.params.getMoveContentListener()::contentMoved );
        }

        if ( params.stopInherit() )
        {
            moveParams.processor( InheritedContentDataProcessor.PARENT );
        }

        final MoveNodeResult movedNode = nodeService.move( moveParams.build() );

        final Content movedContent = translator.fromNode( movedNode.getMovedNodes().getFirst().getNode() );

        return MoveContentsResult.create().setContentName( movedContent.getDisplayName() ).addMoved( movedContent.getId() ).build();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final MoveContentParams params;

        Builder( final MoveContentParams params )
        {
            this.params = params;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( params, "params cannot be null" );
        }

        public MoveContentCommand build()
        {
            validate();
            return new MoveContentCommand( this );
        }
    }

}
