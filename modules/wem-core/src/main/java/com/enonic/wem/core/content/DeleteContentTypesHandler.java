package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.DeleteContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentTypeDao;

@Component
public final class DeleteContentTypesHandler
    extends CommandHandler<DeleteContentTypes>
{
    private ContentTypeDao contentTypeDao;

    public DeleteContentTypesHandler()
    {
        super( DeleteContentTypes.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteContentTypes command )
        throws Exception
    {
        final QualifiedContentTypeNames contentTypeNames = command.getNames();
        final Session session = context.getJcrSession();
        final int deleted = contentTypeDao.deleteContentType( session, contentTypeNames );

        session.save();
        command.setResult( deleted );
    }

    @Autowired
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
