package com.enonic.xp.core.impl.content;


import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;

import static com.enonic.xp.core.impl.content.ContentNodeHelper.translateNodePathToContentPath;


final class RenameContentCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final RenameContentParams params;

    private final PageDescriptorService pageDescriptorService;

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    private final ContentDataSerializer contentDataSerializer;

    private RenameContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.pageDescriptorService = builder.pageDescriptorService;
        this.partDescriptorService = builder.partDescriptorService;
        this.layoutDescriptorService = builder.layoutDescriptorService;
        this.contentDataSerializer = builder.contentDataSerializer;
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

    private Content doExecute()
    {
        final NodeId nodeId = NodeId.from( params.getContentId() );

        final NodeName nodeName = NodeName.from( params.getNewName().toString() );

        final RenameNodeParams.Builder builder = RenameNodeParams.create().nodeId( nodeId ).refresh( RefreshMode.SEARCH ).nodeName( nodeName );

        if ( params.stopInherit() )
        {
            builder.processor( new RenameContentProcessor() );
        }

        final Node node = nodeService.rename( builder.build() );

        final Content content = translator.fromNode( node, true );

        final ValidationErrors validationErrors = validateContent( content );

        if ( content.isValid() == validationErrors.hasErrors() || !validationErrors.equals( content.getValidationErrors() ) )
        {
            return updateValidState( content );
        }
        else
        {
            return content;
        }
    }

    private ValidationErrors validateContent( final Content content )
    {
        return ValidateContentDataCommand.create()
            .data( content.getData() )
            .extraDatas( content.getAllExtraData() )
            .contentTypeName( content.getType() )
            .contentName( content.getName() )
            .displayName( content.getDisplayName() )
            .contentTypeService( this.contentTypeService )
            .contentValidators( this.contentValidators )
            .build()
            .execute();
    }

    private Content updateValidState( final Content content )
    {
        final UpdateContentParams updateContentParams = new UpdateContentParams().requireValid( false )
            .contentId( content.getId() )
            .stopInherit( false )
            .editor( edit -> edit.valid = !content.isValid() );

        return UpdateContentCommand.create( updateContentParams )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .translator( this.translator )
            .contentEventProducer( contentEventProducer )
            .siteService( siteService )
            .contentTypeService( contentTypeService )
            .xDataService( this.xDataService )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .contentDataSerializer( this.contentDataSerializer )
            .build()
            .execute();
    }

    public static class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private final RenameContentParams params;

        private PageDescriptorService pageDescriptorService;

        private PartDescriptorService partDescriptorService;

        private LayoutDescriptorService layoutDescriptorService;

        private ContentDataSerializer contentDataSerializer;

        Builder( final RenameContentParams params )
        {
            this.params = params;
        }

        Builder pageDescriptorService( final PageDescriptorService value )
        {
            this.pageDescriptorService = value;
            return this;
        }

        Builder partDescriptorService( final PartDescriptorService value )
        {
            this.partDescriptorService = value;
            return this;
        }

        Builder layoutDescriptorService( final LayoutDescriptorService value )
        {
            this.layoutDescriptorService = value;
            return this;
        }

        Builder contentDataSerializer( final ContentDataSerializer value )
        {
            this.contentDataSerializer = value;
            return this;
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

