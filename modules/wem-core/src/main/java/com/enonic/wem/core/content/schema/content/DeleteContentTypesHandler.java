package com.enonic.wem.core.content.schema.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.schema.content.DeleteContentTypes;
import com.enonic.wem.api.content.schema.content.ContentTypeDeletionResult;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.exception.ContentTypeNotFoundException;
import com.enonic.wem.api.exception.UnableToDeleteContentTypeException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.schema.content.dao.ContentTypeDao;

@Component
public final class DeleteContentTypesHandler
    extends CommandHandler<DeleteContentTypes>
{
    private ContentTypeDao contentTypeDao;

    private ContentDao contentDao;

    public DeleteContentTypesHandler()
    {
        super( DeleteContentTypes.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteContentTypes command )
        throws Exception
    {
        final ContentTypeDeletionResult contentTypeDeletionResult = new ContentTypeDeletionResult();

        for ( QualifiedContentTypeName qualifiedContentTypeName : command.getNames() )
        {
            try
            {
                if ( contentDao.countContentTypeUsage( qualifiedContentTypeName, context.getJcrSession() ) > 0 )
                {
                    Exception e = new UnableToDeleteContentTypeException( qualifiedContentTypeName, "Content type is being used." );
                    contentTypeDeletionResult.failure( qualifiedContentTypeName, e );
                }
                else
                {
                    contentTypeDao.delete( qualifiedContentTypeName, context.getJcrSession() );
                    contentTypeDeletionResult.success( qualifiedContentTypeName );
                    context.getJcrSession().save();
                }
            }
            catch ( ContentTypeNotFoundException e )
            {
                contentTypeDeletionResult.failure( qualifiedContentTypeName, e );
            }
        }

        command.setResult( contentTypeDeletionResult );
    }

    @Autowired
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }

    @Autowired
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
