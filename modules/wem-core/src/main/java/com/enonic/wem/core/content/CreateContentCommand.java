package com.enonic.wem.core.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentAlreadyExistException;
import com.enonic.wem.api.content.ContentCreatedEvent;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeAlreadyExistException;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.core.media.MediaInfo;

final class CreateContentCommand
    extends AbstractContentCommand
{
    private final static Logger LOG = LoggerFactory.getLogger( CreateContentCommand.class );

    private final CreateContentParams params;

    private final MediaInfo mediaInfo;

    private CreateContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.mediaInfo = builder.mediaInfo;
    }

    Content execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Content doExecute()
    {
        if ( !params.isDraft() )
        {
            validateContentData( params );
        }

        if ( params.getForm() == null )
        {
            final ContentType contentType = contentTypeService.getByName( new GetContentTypeParams().contentTypeName( params.getType() ) );
            params.form( contentType.form() );
        }

        final CreateContentParams handledParams = new ProxyContentProcessor( mediaInfo ).processCreateMedia( params );

        final CreateNodeParams createNodeParams = translator.toCreateNode( handledParams );

        final Node createdNode;
        try
        {
            createdNode = nodeService.create( createNodeParams );
            eventPublisher.publish( new ContentCreatedEvent( ContentId.from( createdNode.id().toString() ) ) );
        }
        catch ( NodeAlreadyExistException e )
        {
            throw new ContentAlreadyExistException( ContentPath.from( params.getParent(), params.getName().toString() ) );
        }

        return translator.fromNode( createdNode );
    }

    private void validateContentData( final CreateContentParams contentParams )
    {
        final DataValidationErrors dataValidationErrors = validate( contentParams.getType(), contentParams.getData() );

        for ( DataValidationError error : dataValidationErrors )
        {
            LOG.info( "*** DataValidationError: " + error.getErrorMessage() );
        }
        if ( dataValidationErrors.hasErrors() )
        {
            throw new ContentDataValidationException( dataValidationErrors.getFirst().getErrorMessage() );
        }
    }

    static Builder create()
    {
        return new Builder();
    }

    static Builder create( AbstractContentCommand source )
    {
        return new Builder( source );
    }

    static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private CreateContentParams params;

        private MediaInfo mediaInfo;

        private Builder()
        {
            // nothing
        }

        private Builder( AbstractContentCommand source )
        {
            super( source );
        }

        Builder params( final CreateContentParams params )
        {
            this.params = params;
            return this;
        }

        Builder mediaInfo( final MediaInfo value )
        {
            this.mediaInfo = value;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params, "params must be given" );
        }

        public CreateContentCommand build()
        {
            validate();
            return new CreateContentCommand( this );
        }
    }

}
