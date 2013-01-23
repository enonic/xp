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
    public void create( final ContentType contentType, final Session session )
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
    public void update( final ContentType contentType, final Session session )
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
    public void delete( final QualifiedContentTypeName qualifiedContentTypeName, final Session session )
    {
        try
        {
            new DeleteContentTypeDaoHandler( session ).handle( qualifiedContentTypeName );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to delete content type [{0}]", qualifiedContentTypeName );
        }
    }

    @Override
    public ContentTypes selectAll( final Session session )
    {
        try
        {
            return new RetrieveContentTypeDaoHandler( session ).retrieveAll();
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve all content types" );
        }
    }

    @Override
    public ContentTypes select( final QualifiedContentTypeNames qualifiedContentTypeNames, final Session session )
    {
        try
        {
            return new RetrieveContentTypeDaoHandler( session ).retrieve( qualifiedContentTypeNames );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve content types [{0}]", qualifiedContentTypeNames );
        }
    }
}
