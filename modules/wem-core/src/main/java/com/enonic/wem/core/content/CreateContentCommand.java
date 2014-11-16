package com.enonic.wem.core.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentAlreadyExistException;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.repo.CreateNodeParams;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodeAlreadyExistException;

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
        if ( !params.isDraft() )
        {
            validateContentData( params );
        }

        if ( params.getForm() == null )
        {
            final ContentType contentType =
                contentTypeService.getByName( new GetContentTypeParams().contentTypeName( params.getContentType() ) );
            params.form( contentType.form() );
        }

        final CreateNodeParams createNodeParams = translator.toCreateNode( params );

        final Node createdNode;
        try
        {
            createdNode = nodeService.create( createNodeParams );
        }
        catch ( NodeAlreadyExistException e )
        {
            throw new ContentAlreadyExistException( ContentPath.from( params.getParentContentPath(), params.getName().toString() ) );
        }

        return translator.fromNode( createdNode );
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
