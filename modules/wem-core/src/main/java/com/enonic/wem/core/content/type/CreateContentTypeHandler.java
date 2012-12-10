package com.enonic.wem.core.content.type;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.type.CreateContentType;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;

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
        final ContentType contentType = command.getContentType();
        final Session session = context.getJcrSession();
        contentTypeDao.createContentType( contentType, session );
        session.save();
        command.setResult( contentType.getQualifiedName() );
    }

    @Autowired
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
