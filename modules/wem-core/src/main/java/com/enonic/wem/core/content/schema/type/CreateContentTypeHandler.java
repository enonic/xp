package com.enonic.wem.core.content.schema.type;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.schema.type.CreateContentType;
import com.enonic.wem.api.content.schema.type.ContentType;
import com.enonic.wem.api.content.schema.type.ContentTypeFetcher;
import com.enonic.wem.api.content.schema.type.validator.ContentTypeValidationResult;
import com.enonic.wem.api.content.schema.type.validator.ContentTypeValidator;
import com.enonic.wem.api.content.schema.type.validator.InvalidContentTypeException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.type.dao.ContentTypeDao;

import static com.enonic.wem.api.content.schema.type.ContentType.newContentType;
import static com.enonic.wem.api.content.schema.type.validator.ContentTypeValidator.newContentTypeValidator;

@Component
public final class CreateContentTypeHandler
    extends CommandHandler<CreateContentType>
{
    private ContentTypeDao contentTypeDao;

    public CreateContentTypeHandler()
    {
        super( CreateContentType.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateContentType command )
        throws Exception
    {
        final DateTime currentTime = DateTime.now();

        final ContentType contentType = newContentType().
            name( command.getName() ).
            displayName( command.getDisplayName() ).
            module( command.getModuleName() ).
            superType( command.getSuperType() ).
            setAbstract( command.isAbstract() ).
            setFinal( command.isFinal() ).
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

    @Autowired
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
