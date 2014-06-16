package com.enonic.wem.core.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.entity.CreateNodeParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;

final class CreateContentCommand
    extends AbstractContentCommand
{
    private final static Logger LOG = LoggerFactory.getLogger( CreateContentCommand.class );

    private final CreateContentParams params;

    private CreateContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    Content execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Content doExecute()
    {
        if ( !this.params.isDraft() )
        {
            validateContentData( this.params );
        }

        final CreateNodeParams createNodeParams = getTranslator().toCreateNode( this.params );
        final Node createdNode = nodeService.create( createNodeParams, ContentConstants.DEFAULT_CONTEXT );

        return getTranslator().fromNode( createdNode );
    }

    private void validateContentData( final CreateContentParams contentParams )
    {
        final DataValidationErrors dataValidationErrors = validate( contentParams.getContentType(), contentParams.getContentData() );

        for ( DataValidationError error : dataValidationErrors )
        {
            LOG.info( "*** DataValidationError: " + error.getErrorMessage() );
        }
        if ( dataValidationErrors.hasErrors() )
        {
            throw new ContentDataValidationException( dataValidationErrors.getFirst().getErrorMessage() );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private CreateContentParams params;

        public Builder setParams( final CreateContentParams params )
        {
            this.params = params;
            return this;
        }

        public Builder params( final CreateContentParams params )
        {
            this.params = params;
            return this;
        }

        void validate()
        {
            super.validate();
        }

        public CreateContentCommand build()
        {
            validate();
            return new CreateContentCommand( this );
        }
    }

}
