package com.enonic.wem.core.schema.content.dao;


final class ContentTypeDaoImpl
 {
      /*
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
    public void delete( final ContentTypeName contentTypeName, final Session session )
    {
        try
        {
            new ContentTypeDaoHandlerDelete( session ).handle( contentTypeName );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to delete content type [{0}]", contentTypeName );
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
    public ContentTypes select( final ContentTypeNames contentTypeNames, final Session session )
    {
        try
        {
            return new ContentTypeDaoHandlerSelect( session ).select( contentTypeNames );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve content types [{0}]", contentTypeNames );
        }
    }

    @Override
    public ContentType select( final ContentTypeName contentTypeName, final Session session )
    {
        try
        {
            final ContentTypeNames contentTypeNames = ContentTypeNames.from( contentTypeName );
            return new ContentTypeDaoHandlerSelect( session ).select( contentTypeNames ).first();
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve content type [{0}]", contentTypeName );
        }
    }
    */
}
