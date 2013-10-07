package com.enonic.wem.core.schema.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.schema.content.DeleteContentType;
import com.enonic.wem.api.command.schema.content.DeleteContentTypeResult;
import com.enonic.wem.api.exception.ContentTypeNotFoundException;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;


public final class DeleteContentTypeHandler
    extends CommandHandler<DeleteContentType>
{
    private ContentTypeDao contentTypeDao;

    private ContentDao contentDao;

    @Override
    public void handle()
        throws Exception
    {
        final QualifiedContentTypeName qualifiedContentTypeName = command.getName();
        if ( contentDao.countContentTypeUsage( qualifiedContentTypeName, context.getJcrSession() ) > 0 )
        {
            command.setResult( DeleteContentTypeResult.UNABLE_TO_DELETE );
        }
        else
        {
            try
            {
                contentTypeDao.delete( qualifiedContentTypeName, context.getJcrSession() );
                context.getJcrSession().save();
                command.setResult( DeleteContentTypeResult.SUCCESS );
            }
            catch ( ContentTypeNotFoundException e )
            {
                command.setResult( DeleteContentTypeResult.NOT_FOUND );
            }
        }
    }

    @Inject
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
