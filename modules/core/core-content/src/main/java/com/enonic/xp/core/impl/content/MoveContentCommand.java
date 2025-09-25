package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentAlreadyMovedException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.MoveContentException;
import com.enonic.xp.content.MoveContentListener;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.MoveNodeListener;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;

final class MoveContentCommand
    extends AbstractContentCommand
    implements MoveNodeListener
{
    private final MoveContentParams params;

    private final MoveContentListener moveContentListener;

    private MoveContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.moveContentListener = builder.moveContentListener;
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

        final MoveNodeParams.Builder builder =
            MoveNodeParams.create().nodeId( sourceNodeId ).parentNodePath( newParentPath ).refresh( RefreshMode.ALL ).moveListener( this );

        if ( params.stopInherit() )
        {
            builder.processor( InheritedContentDataProcessor.PARENT );
        }

        final Node movedNode = nodeService.move( builder.build() );

        final Content movedContent = translator.fromNode( movedNode );

        return MoveContentsResult.create().setContentName( movedContent.getDisplayName() ).addMoved( movedContent.getId() ).build();
    }

    @Override
    public void nodesMoved( final int count )
    {
        if ( moveContentListener != null )
        {
            moveContentListener.contentMoved( count );
        }
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final MoveContentParams params;

        private MoveContentListener moveContentListener;

        Builder( final MoveContentParams params )
        {
            this.params = params;
        }

        public Builder moveListener( final MoveContentListener moveContentListener )
        {
            this.moveContentListener = moveContentListener;
            return this;
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
