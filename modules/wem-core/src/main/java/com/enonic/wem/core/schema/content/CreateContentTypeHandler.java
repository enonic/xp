package com.enonic.wem.core.schema.content;

import javax.inject.Inject;
import javax.jcr.Session;

import org.joda.time.DateTime;

import com.enonic.wem.api.command.schema.content.CreateContentType;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeFetcher;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidator;
import com.enonic.wem.api.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.validator.ContentTypeValidator.newContentTypeValidator;


public final class CreateContentTypeHandler
    extends CommandHandler<CreateContentType>
{
    private ContentTypeDao contentTypeDao;

    @Override
    public void handle( final CreateContentType command )
        throws Exception
    {
        final DateTime currentTime = DateTime.now();

        final ContentType contentType = newContentType().
            name( command.getName() ).
            displayName( command.getDisplayName() ).
            contentDisplayNameScript( command.getContentDisplayNameScript() ).
            superType( command.getSuperType() ).
            setAbstract( command.isAbstract() ).
            setFinal( command.isFinal() ).
            allowChildContent( command.getAllowChildContent() ).
            builtIn( command.isBuiltIn() ).
            icon( command.getIcon() ).
            createdTime( currentTime ).
            modifiedTime( currentTime ).
            form( command.getForm() ).
            build();

        final Session session = context.getJcrSession();

        validate( contentType, session );

        contentTypeDao.create( contentType, session );
        session.save();

        command.setResult( contentType.getQualifiedName() );
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
