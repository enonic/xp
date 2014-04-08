package com.enonic.wem.core.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentDataValidationException;
import com.enonic.wem.api.content.CreateContentParams2;
import com.enonic.wem.api.entity.CreateNodeParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;

final class CreateContentCommand
    extends AbstractContentCommand<CreateContentCommand>
{
    private final static Logger LOG = LoggerFactory.getLogger( CreateContentCommand.class );

    private CreateContentParams2 params;

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
        final Node createdNode = nodeService.create( createNodeParams ).getPersistedNode();

        return getTranslator().fromNode( createdNode );
    }

    private void validateContentData( final CreateContentParams2 contentParams )
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

    CreateContentCommand params( final CreateContentParams2 params )
    {
        this.params = params;
        return this;
    }
}
