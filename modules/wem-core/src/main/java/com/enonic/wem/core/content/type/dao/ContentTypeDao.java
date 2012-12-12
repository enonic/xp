package com.enonic.wem.core.content.type.dao;

import javax.jcr.Session;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;

public interface ContentTypeDao
{
    public void createContentType( ContentType contentType, Session session );

    public void updateContentType( ContentType contentType, Session session );

    public void deleteContentType( QualifiedContentTypeName qualifiedContentTypeName, Session session );

    public ContentTypes retrieveAllContentTypes( Session session );

    public ContentTypes retrieveContentTypes( QualifiedContentTypeNames qualifiedContentTypeNames, Session session );
}
