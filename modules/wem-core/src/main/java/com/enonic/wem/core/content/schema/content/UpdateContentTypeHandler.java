package com.enonic.wem.core.content.schema.content;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.content.schema.content.UpdateContentType;
import com.enonic.wem.api.command.content.schema.content.UpdateContentTypeResult;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.ContentTypeFetcher;
import com.enonic.wem.api.content.schema.content.editor.ContentTypeEditor;
import com.enonic.wem.api.content.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.content.schema.content.validator.ContentTypeValidator;
import com.enonic.wem.api.content.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.api.exception.ContentTypeNotFoundException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.content.dao.ContentTypeDao;

import static com.enonic.wem.api.content.schema.content.validator.ContentTypeValidator.newContentTypeValidator;


public final class UpdateContentTypeHandler
    extends CommandHandler<UpdateContentType>
{
    private ContentTypeDao contentTypeDao;

    public UpdateContentTypeHandler()
    {
        super( UpdateContentType.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateContentType command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        final ContentTypeEditor editor = command.getEditor();

        try
        {
            final ContentType persistedContentType = contentTypeDao.select( command.getQualifiedName(), session );
            if ( persistedContentType == null )
            {
                throw new ContentTypeNotFoundException( command.getQualifiedName() );
            }

            final ContentType edited = editor.edit( persistedContentType );
            if ( edited != null )
            {
                validate( edited, session );
                contentTypeDao.update( edited, session );
                session.save();
                command.setResult( UpdateContentTypeResult.SUCCESS );
            }
        }
        catch ( ContentTypeNotFoundException e )
        {
            UpdateContentTypeResult.from( e );
        }
    }

    private void validate( final ContentType contentType, final Session session )
    {
        final ContentTypeFetcher fetcher = new InternalContentTypeFetcher( session, contentTypeDao );
        final ContentTypeValidator validator = newContentTypeValidator().contentTypeFetcher( fetcher ).build();
        validator.validate( contentType );
        final ContentTypeValidationResult validationResult = validator.getResult();

        if ( !validationResult.hasErrors() )
        {
            return;
        }

        throw new InvalidContentTypeException( contentType, validationResult.getFirst().getErrorMessage() );
    }

    @Inject
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
