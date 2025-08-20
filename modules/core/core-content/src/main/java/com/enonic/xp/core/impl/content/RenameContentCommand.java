package com.enonic.xp.core.impl.content;


import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.core.impl.content.serializer.ValidationErrorsSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeDataProcessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.schema.content.ContentTypeName;

import static com.enonic.xp.core.impl.content.ContentNodeHelper.translateNodePathToContentPath;


final class RenameContentCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final RenameContentParams params;

    private RenameContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create( final RenameContentParams params )
    {
        return new Builder( params );
    }

    Content execute()
    {
        try
        {
            return doExecute();
        }
        catch ( final NodeAlreadyExistAtPathException e )
        {
            final ContentPath path = translateNodePathToContentPath( e.getNode() );
            throw new ContentAlreadyExistsException( path, e.getRepositoryId(), e.getBranch() );
        }
    }

    private NodeDataProcessor initProcessors()
    {
        final List<NodeDataProcessor> processors = new ArrayList<>();

        if ( params.stopInherit() )
        {
            processors.add( new InheritedContentDataProcessor()
            {
                @Override
                protected EnumSet<ContentInheritType> getTypesToProceed()
                {
                    return EnumSet.of( ContentInheritType.NAME );
                }
            } );
        }

        processors.add( ( data, newNodePath ) -> {
            final PropertyTree contentData = data.getProperty( ContentPropertyNames.DATA ).getSet().toTree();
            final String displayName = data.getProperty( ContentPropertyNames.DISPLAY_NAME ).getString();
            final ContentTypeName type = ContentTypeName.from( data.getProperty( ContentPropertyNames.TYPE ).getString() );
            final ExtraDatas extraData = data.hasProperty( ContentPropertyNames.EXTRA_DATA ) ? new ContentDataSerializer().fromExtraData(
                data.getProperty( ContentPropertyNames.EXTRA_DATA ).getSet() ) : null;

            final ValidationErrors validationErrors = ValidateContentDataCommand.create()
                .data( contentData )
                .extraDatas( extraData )
                .contentTypeName( type )
                .contentName( ContentName.from( newNodePath.getName().toString() ) )
                .displayName( displayName )
                .contentTypeService( contentTypeService )
                .contentValidators( contentValidators )
                .build()
                .execute();

            data.setProperty( ContentPropertyNames.VALID, ValueFactory.newBoolean( !validationErrors.hasErrors() ) );
            new ValidationErrorsSerializer().toData( validationErrors, data.getRoot() );

            return data;
        } );

        return new CompositeNodeDataProcessor( processors );
    }

    private Content doExecute()
    {
        final NodeId nodeId = NodeId.from( params.getContentId() );

        final NodeName nodeName = NodeName.from( params.getNewName() );

        final RenameNodeParams.Builder builder =
            RenameNodeParams.create().nodeId( nodeId ).refresh( RefreshMode.ALL ).nodeName( nodeName ).processor( initProcessors() );

        final Node node = nodeService.rename( builder.build() );

        return translator.fromNode( node, true );
    }

    public static class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private final RenameContentParams params;

        Builder( final RenameContentParams params )
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

