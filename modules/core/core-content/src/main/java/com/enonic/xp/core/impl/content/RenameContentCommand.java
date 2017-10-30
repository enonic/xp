package com.enonic.xp.core.impl.content;


import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.core.impl.content.validate.ValidationErrors;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.page.PageDescriptorService;

import static com.enonic.xp.core.impl.content.ContentNodeHelper.translateNodePathToContentPath;


final class RenameContentCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final RenameContentParams params;

    private final PageDescriptorService pageDescriptorService;

    private RenameContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.pageDescriptorService = builder.pageDescriptorService;
    }

    public static Builder create( final RenameContentParams params )
    {
        return new Builder( params );
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
            throw new ContentAlreadyExistsException( path );
        }
    }

    private Content doExecute()
    {
        final NodeId nodeId = NodeId.from( params.getContentId() );

        final NodeName nodeName = NodeName.from( params.getNewName().toString() );

        final Node node = nodeService.rename( RenameNodeParams.create().
            nodeId( nodeId ).
            nodeName( nodeName ).
            build() );

        final Content content = translator.fromNode( node, false );

        final boolean isValid = validateContent( content );

        if ( content.isValid() != isValid )
        {
            return updateValidState( content, isValid );
        }

        return getContent( params.getContentId() );
    }

    private boolean validateContent( final Content content )
    {
        final ValidationErrors validationErrors = ValidateContentDataCommand.create().
            contentData( content.getData() ).
            contentType( content.getType() ).
            name( content.getName() ).
            displayName( content.getDisplayName() ).
            extradatas( content.getAllExtraData() ).
            contentTypeService( this.contentTypeService ).
            mixinService( this.mixinService ).
            siteService( this.siteService ).
            build().
            execute();

        return validationErrors.hasErrors() ? false : true;
    }

    private Content updateValidState( final Content content, final boolean isValid )
    {

        final UpdateContentParams updateContentParams = new UpdateContentParams().
            requireValid( false ).
            contentId( content.getId() ).
            modifier( content.getModifier() ).
            editor( edit -> edit.valid = isValid );

        return UpdateContentCommand.create( this ).params( updateContentParams ).
            siteService( siteService ).
            contentTypeService( contentTypeService ).
            pageDescriptorService( this.pageDescriptorService ).
            build().execute();
    }

    public static class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private final RenameContentParams params;

        private PageDescriptorService pageDescriptorService;

        public Builder( final RenameContentParams params )
        {
            this.params = params;
        }

        Builder pageDescriptorService( final PageDescriptorService value )
        {
            this.pageDescriptorService = value;
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

