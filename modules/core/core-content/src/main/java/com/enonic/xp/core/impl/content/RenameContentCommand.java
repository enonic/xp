package com.enonic.xp.core.impl.content;


import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ModifyContentParams;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.RenameNodeParams;

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

    private Content doExecute()
    {
        final NodeId nodeId = NodeId.from( params.getContentId() );

        final NodeName nodeName = NodeName.from( params.getNewName().toString() );

        final RenameNodeParams.Builder builder = RenameNodeParams.create().nodeId( nodeId ).refresh( RefreshMode.ALL ).nodeName( nodeName );

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
        final ModifyContentParams updateContentParams = ModifyContentParams.create()
            .contentId( content.getId() ).modifier( edit -> edit.valid.setValue( !content.isValid() ) ).build();

        return ModifyContentCommand.create( this )
            .params( updateContentParams )
            .siteService( siteService )
            .contentTypeService( contentTypeService )
            .xDataService( this.xDataService )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .build().execute().getResult( ContextAccessor.current().getBranch() );
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

