package com.enonic.xp.core.impl.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.MoveContentException;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.core.impl.content.serializer.ValidationErrorsSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeDataProcessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.page.Page;
import com.enonic.xp.schema.content.ContentTypeName;

import static com.enonic.xp.core.impl.content.ContentNodeHelper.translateNodePathToContentPath;

final class MoveContentCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final MoveContentParams params;

    private final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

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
            throw new ContentAlreadyExistsException( translateNodePathToContentPath( e.getNode() ), e.getRepositoryId(), e.getBranch() );
        }
        catch ( NodeAccessException e )
        {
            throw ContentNodeHelper.toContentAccessException( e );
        }
    }

    private MoveContentsResult doExecute()
    {
        final ContentId contentId = params.getContentId();
        final Content sourceContent = getContent( contentId );

        final List<String> modifiedFields = new ArrayList<>();
        final var processors = CompositeNodeDataProcessor.create().add( updateValid() );

        final NodePath newParentPath;
        if ( params.getParentContentPath() == null || params.getParentContentPath().equals( sourceContent.getParentPath() ) )
        {
            newParentPath = null;
        }
        else
        {
            if ( !layersSync )
            {
                processors.add( InheritedContentDataProcessor.PARENT );
                processors.add( PublishedDataProcessor::removePublished );
            }
            validateParentChildRelations( params.getParentContentPath(), sourceContent.getType() );
            newParentPath = ContentNodeHelper.translateContentPathToNodePath( params.getParentContentPath() );
            modifiedFields.add( "parentPath" );
        }

        final NodeName newNodeName;
        if ( params.getNewName() == null || params.getNewName().equals( sourceContent.getName() ) )
        {
            newNodeName = null;
        }
        else
        {
            if ( !layersSync )
            {
                processors.add( InheritedContentDataProcessor.NAME );
            }
            newNodeName = NodeName.from( params.getNewName() );
            modifiedFields.add( "name" );
        }

        final NodeId sourceNodeId = NodeId.from( contentId );

        final MoveNodeParams.Builder moveParams = MoveNodeParams.create()
            .nodeId( sourceNodeId )
            .newName( newNodeName )
            .newParentPath( newParentPath )
            .processor( processors.build() )
            .refresh( RefreshMode.ALL );

        if ( layersSync )
        {
            moveParams.versionAttributes( ContentAttributesHelper.layersSyncAttr() )
                .childVersionAttributes( ContentAttributesHelper.layersSyncAttr() );
        }
        else
        {
            moveParams.versionAttributes(
                    ContentAttributesHelper.versionHistoryAttr( ContentAttributesHelper.MOVE_ATTR, modifiedFields.toArray( String[]::new ) ) )
                .childVersionAttributes( ContentAttributesHelper.versionHistoryAttr( ContentAttributesHelper.MOVE_ATTR, "parentPath" ) );
        }

        if ( params.getMoveContentListener() != null )
        {
            moveParams.moveListener( this.params.getMoveContentListener()::contentMoved );
        }

        final MoveNodeResult movedNode = nodeService.move( moveParams.build() );

        final Content movedContent = ContentNodeTranslator.fromNode( movedNode.getMovedNodes().getFirst().getNode() );

        return MoveContentsResult.create().setContentName( movedContent.getDisplayName() ).addMoved( movedContent.getId() ).build();
    }

    private NodeDataProcessor updateValid()
    {
        return ( data, nodePath ) -> {
            data = data.copy();
            final PropertyTree contentData = data.getProperty( ContentPropertyNames.DATA ).getSet().toTree();
            final String displayName = data.getProperty( ContentPropertyNames.DISPLAY_NAME ).getString();
            final ContentTypeName type = ContentTypeName.from( data.getProperty( ContentPropertyNames.TYPE ).getString() );
            final ExtraDatas extraData = data.hasProperty( ContentPropertyNames.EXTRA_DATA ) ? contentDataSerializer.fromExtraData(
                data.getProperty( ContentPropertyNames.EXTRA_DATA ).getSet() ) : null;
            final Page page = data.hasProperty( ContentPropertyNames.PAGE ) ? contentDataSerializer.fromPageData(
                data.getProperty( ContentPropertyNames.PAGE ).getSet() ) : null;

            final ValidationErrors validationErrors = ValidateContentDataCommand.create()
                .data( contentData )
                .extraDatas( extraData )
                .contentTypeName( type )
                .contentName( ContentName.from( nodePath.getName().toString() ) )
                .displayName( displayName )
                .page( page )
                .contentTypeService( contentTypeService )
                .contentValidators( contentValidators )
                .build()
                .execute();

            data.setProperty( ContentPropertyNames.VALID, ValueFactory.newBoolean( !validationErrors.hasErrors() ) );
            new ValidationErrorsSerializer().toData( validationErrors, data.getRoot() );

            return data;
        };
    }

    static class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
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

        MoveContentCommand build()
        {
            validate();
            return new MoveContentCommand( this );
        }
    }
}
