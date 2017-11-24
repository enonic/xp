package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentAlreadyMovedException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.MoveContentException;
import com.enonic.xp.content.MoveContentListener;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.MoveNodeListener;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeAlreadyMovedException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.site.Site;

final class MoveContentCommand
    extends AbstractContentCommand
    implements MoveNodeListener
{
    private final MoveContentParams params;

    private final ContentService contentService;

    private final MoveContentListener moveContentListener;

    private MoveContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.contentService = builder.contentService;
        this.moveContentListener = builder.moveContentListener;
    }

    public static Builder create( final MoveContentParams params )
    {
        return new Builder( params );
    }

    MoveContentsResult execute()
    {
        params.validate();

        try
        {
            final MoveContentsResult movedContents = doExecute();
            this.nodeService.refresh( RefreshMode.ALL );
            return movedContents;
        }
        catch ( NodeAlreadyMovedException e )
        {
            throw new ContentAlreadyMovedException( e.getMessage() );
        }
        catch ( MoveNodeException e )
        {
            throw new MoveContentException( e.getMessage() );
        }
        catch ( NodeAlreadyExistAtPathException e )
        {
            throw new MoveContentException( "Content already exists at path: " + e.getNode().toString() );
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }
    }

    private MoveContentsResult doExecute()
    {
        this.verifyIntegrity( params.getParentContentPath() );

        final NodeId sourceNodeId = NodeId.from( params.getContentId().toString() );
        final Node sourceNode = nodeService.getById( sourceNodeId );
        if ( sourceNode == null )
        {
            throw new IllegalArgumentException( String.format( "Content with id [%s] not found", params.getContentId() ) );
        }

        final Site nearestSite = contentService.getNearestSite( params.getContentId() );

        final NodePath nodePath = NodePath.create( ContentConstants.CONTENT_ROOT_PATH ).
            elements( params.getParentContentPath().toString() ).
            build();

        if ( sourceNode.parentPath().equals( nodePath ) )
        {
            throw new NodeAlreadyMovedException(
                String.format( "Content with id [%s] is already a child of [%s]", params.getContentId(), params.getParentContentPath() ) );
        }

        final ContentPath newParentPath = ContentNodeHelper.translateNodePathToContentPath( nodePath );

        final boolean isOutOfSite =
            nearestSite != null && ( !newParentPath.isChildOf( nearestSite.getPath() ) || !newParentPath.equals( nearestSite.getPath() ) );

        checkRestrictedMoves( sourceNode, isOutOfSite );

        final Node movedNode = nodeService.move( sourceNodeId, nodePath, this );

        final Content movedContent = translator.fromNode( movedNode, true );

        String contentName = movedContent.getDisplayName();
        ContentId contentId = movedContent.getId();

        if ( isOutOfSite )
        {
            final UpdateContentParams updateParams = new UpdateContentParams().
                contentId( params.getContentId() ).
                modifier( params.getCreator() ).
                editor( edit -> edit.extraDatas = this.updateExtraData( nearestSite, movedContent ) );
            final Content updatedContent = contentService.update( updateParams );

            contentName = updatedContent.getDisplayName();
            contentId = updatedContent.getId();
        }

        final MoveContentsResult result = MoveContentsResult.create().
            setContentName( contentName ).
            addMoved( contentId ).
            build();

        return result;
    }

    private void verifyIntegrity( ContentPath destinationPath )
    {
        if ( !destinationPath.isRoot() )
        {
            final Content parent = contentService.getByPath( destinationPath );
            if ( parent == null )
            {
                throw new IllegalArgumentException(
                    "Content could not be moved. Children not allowed in destination [" + destinationPath.toString() + "]" );
            }
            final ContentType parentContentType =
                contentTypeService.getByName( new GetContentTypeParams().contentTypeName( parent.getType() ) );
            if ( !parentContentType.allowChildContent() )
            {
                throw new IllegalArgumentException(
                    "Content could not be moved. Children not allowed in destination [" + destinationPath.toString() + "]" );
            }
        }
    }

    private void checkRestrictedMoves( final Node existingNode, final Boolean isOutOfSite )
    {
        if ( translator.fromNode( existingNode, false ).getType().isFragment() )
        {
            if ( isOutOfSite )
            {
                throw new MoveContentException( "A Fragment is not allowed to be moved out of its site." );
            }
        }
    }

    private ExtraDatas updateExtraData( Site site, Content content )
    {
        return ExtraDatas.from( content.getAllExtraData().stream().filter(
            extraData -> site.getSiteConfigs().get( extraData.getName().getApplicationKey() ) == null ) );
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

        private ContentService contentService;

        private MoveContentListener moveContentListener;

        public Builder( final MoveContentParams params )
        {
            this.params = params;
        }

        public MoveContentCommand.Builder contentService( ContentService contentService )
        {
            this.contentService = contentService;
            return this;
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
            Preconditions.checkNotNull( params );
        }

        public MoveContentCommand build()
        {
            validate();
            return new MoveContentCommand( this );
        }
    }

}
