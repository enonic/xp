package com.enonic.wem.core.schema.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.UpdateContentType;
import com.enonic.wem.api.command.schema.content.UpdateContentTypeResult;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeNotFoundException;
import com.enonic.wem.api.schema.content.validator.ContentTypeSuperTypeValidator;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;


public final class UpdateContentTypeHandler
    extends CommandHandler<UpdateContentType>
{
    private ContentTypeDao contentTypeDao;

    @Override
    public void handle()
        throws Exception
    {
        final ContentType persistedContentType =
            context.getClient().execute( Commands.contentType().get().byName().contentTypeName( command.getContentTypeName() ) );

        if ( persistedContentType == null )
        {
            throw new ContentTypeNotFoundException( command.getContentTypeName() );
        }

        final ContentType editedContentType = command.getEditor().edit( persistedContentType );

        if ( editedContentType != null )
        {
            persistedContentType.checkIllegalEdit( editedContentType );
            validate( editedContentType );

            contentTypeDao.updateContentType( editedContentType );

            command.setResult( UpdateContentTypeResult.SUCCESS );
        }

        command.setResult( UpdateContentTypeResult.SUCCESS );
    }

    private void validate( final ContentType contentType )
    {
        final ContentTypeSuperTypeValidator validator = ContentTypeSuperTypeValidator.newContentTypeSuperTypeValidator().
            client( context.getClient() ).
            build();

        validator.validate( contentType.getName(), contentType.getSuperType() );

        final ContentTypeValidationResult validationResult = validator.getResult();

        if ( !validationResult.hasErrors() )
        {
            return;
        }

        throw new InvalidContentTypeException( contentType.getName(), validationResult.getFirst().getErrorMessage() );
    }

    @Inject
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
