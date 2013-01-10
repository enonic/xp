package com.enonic.wem.core.content.type;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.type.ValidateContentType;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeFetcher;
import com.enonic.wem.api.content.type.validator.ContentTypeValidator;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;

import static com.enonic.wem.api.content.type.validator.ContentTypeValidator.newContentTypeValidator;

@Component
public class ValidateContentTypeHandler
    extends CommandHandler<ValidateContentType>
{

    private ContentTypeDao contentTypeDao;

    public ValidateContentTypeHandler()
    {
        super( ValidateContentType.class );
    }

    @Override
    public void handle( final CommandContext context, final ValidateContentType command )
        throws Exception
    {
        Session session = context.getJcrSession();
        ContentTypeFetcher fetcher = new InternalContentTypeFetcher( session, contentTypeDao );
        ContentType contentType = command.getContentType();
        ContentTypeValidator validator = newContentTypeValidator().contentTypeFetcher( fetcher ).build();
        validator.validate( contentType );
        command.setResult( validator.getResult() );
    }

    @Autowired
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
