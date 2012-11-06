package com.enonic.wem.core.content.dao;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeNames;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.exception.SystemException;


@Component
public final class ContentTypeDaoImpl
    implements ContentTypeDao
{

    @Override
    public void createContentType( final Session session, final ContentType contentType )
    {
        try
        {
            new CreateContentTypeDaoHandler( session ).create( contentType );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to create content type [{0}]", contentType );
        }
    }

    @Override
    public ContentTypes retrieveContentTypes( final Session session, final ContentTypeNames contentTypeNames )
    {
        try
        {
            return new RetrieveContentTypeDaoHandler( session ).retrieve( contentTypeNames );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve content types [{0}]", contentTypeNames );
        }
    }

    @Override
    public void updateContentType( final Session session, final ContentType contentType )
    {
        try
        {
            new UpdateContentTypeDaoHandler( session ).update( contentType );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to update content type [{0}]", contentType );
        }
    }

    @Override
    public int deleteContentType( final Session session, final ContentTypeNames contentTypeNames )
    {
        try
        {
            return new DeleteContentTypeDaoHandler( session ).delete( contentTypeNames );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to delete content types [{0}]", contentTypeNames );
        }
    }
}
