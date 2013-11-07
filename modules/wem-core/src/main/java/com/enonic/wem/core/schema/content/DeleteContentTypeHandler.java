package com.enonic.wem.core.schema.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.DeleteContentType;
import com.enonic.wem.api.command.schema.content.DeleteContentTypeResult;
import com.enonic.wem.api.exception.ContentTypeNotFoundException;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;


public final class DeleteContentTypeHandler
    extends CommandHandler<DeleteContentType>
{
    private ContentDao contentDao;

    @Override
    public void handle()
        throws Exception
    {
        final ContentTypeName contentTypeName = command.getName();
        if ( contentDao.countContentTypeUsage( contentTypeName, context.getJcrSession() ) > 0 )
        {
            command.setResult( DeleteContentTypeResult.UNABLE_TO_DELETE );
        }
        else
        {
            try
            {
                context.getClient().execute( Commands.contentType().delete().name( contentTypeName ) );

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
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
