package com.enonic.wem.core.content.type.dao;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
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
    public ContentTypes retrieveContentTypes( final Session session, final QualifiedContentTypeNames contentTypeNames )
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
    public void deleteContentType( final Session session, final QualifiedContentTypeName contentTypeName )
    {
        try
        {
            new DeleteContentTypeDaoHandler( session ).handle( contentTypeName );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to delete content type [{0}]", contentTypeName );
        }
    }

    @Override
    public ContentTypes retrieveAllContentTypes( final Session session )
    {
        try
        {
            return new RetrieveContentTypeDaoHandler( session ).retrieveAll();
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve content types" );
        }
    }
}
