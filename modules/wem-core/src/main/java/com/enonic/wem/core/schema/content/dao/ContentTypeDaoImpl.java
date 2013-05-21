package com.enonic.wem.core.schema.content.dao;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;


public final class ContentTypeDaoImpl
    implements ContentTypeDao
{

    @Override
    public void create( final ContentType contentType, final Session session )
    {
        try
        {
            new ContentTypeDaoHandlerCreate( session ).create( contentType );
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
            new ContentTypeDaoHandlerUpdate( session ).update( contentType );
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
            new ContentTypeDaoHandlerDelete( session ).handle( qualifiedContentTypeName );
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
            return new ContentTypeDaoHandlerSelect( session ).selectAll();
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
            return new ContentTypeDaoHandlerSelect( session ).select( qualifiedContentTypeNames );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve content types [{0}]", qualifiedContentTypeNames );
        }
    }

    @Override
    public ContentType select( final QualifiedContentTypeName qualifiedContentTypeName, final Session session )
    {
        try
        {
            final QualifiedContentTypeNames qualifiedContentTypeNames = QualifiedContentTypeNames.from( qualifiedContentTypeName );
            return new ContentTypeDaoHandlerSelect( session ).select( qualifiedContentTypeNames ).first();
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve content type [{0}]", qualifiedContentTypeName );
        }
    }
}
