package com.enonic.wem.core.schema.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.DeleteNodeByPath;
import com.enonic.wem.api.command.entity.DeleteNodeResult;
import com.enonic.wem.api.command.schema.content.DeleteContentType;
import com.enonic.wem.api.command.schema.content.DeleteContentTypeResult;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.content.dao.ContentDao;


public final class DeleteContentTypeHandler
    extends AbstractContentTypeHandler<DeleteContentType>
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
            final DeleteNodeByPath deleteNodeByPathCommand =
                Commands.node().delete().byPath( new NodePath( "/content-types/" + command.getName().toString() ) );

            final DeleteNodeResult result = context.getClient().execute( deleteNodeByPathCommand );

            switch ( result )
            {
                case SUCCESS:
                    command.setResult( DeleteContentTypeResult.SUCCESS );
                    break;
                case NOT_FOUND:
                    command.setResult( DeleteContentTypeResult.NOT_FOUND );
                    break;
                default:
                    command.setResult( DeleteContentTypeResult.UNABLE_TO_DELETE );
            }
        }
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
