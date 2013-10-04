package com.enonic.wem.core.schema.content;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.schema.content.ValidateContentType;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeFetcher;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidator;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

import static com.enonic.wem.api.schema.content.validator.ContentTypeValidator.newContentTypeValidator;


public class ValidateContentTypeHandler
    extends CommandHandler<ValidateContentType>
{
    private ContentTypeDao contentTypeDao;

    @Override
    public void handle( final ValidateContentType command )
        throws Exception
    {
        Session session = context.getJcrSession();
        ContentTypeFetcher fetcher = new InternalContentTypeFetcher( session, contentTypeDao );
        ContentType contentType = command.getContentType();
        ContentTypeValidator validator = newContentTypeValidator().contentTypeFetcher( fetcher ).build();
        validator.validate( contentType );
        command.setResult( validator.getResult() );
    }

    @Inject
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
