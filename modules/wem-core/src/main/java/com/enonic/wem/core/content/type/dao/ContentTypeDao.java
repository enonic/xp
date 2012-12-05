package com.enonic.wem.core.content.type.dao;

import javax.jcr.Session;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;

public interface ContentTypeDao
{
    public void createContentType( Session session, ContentType contentType );

    public void updateContentType( Session session, ContentType contentType );

    public void deleteContentType( Session session, QualifiedContentTypeName qualifiedContentTypeName );

    public ContentTypes retrieveAllContentTypes( Session session );

    public ContentTypes retrieveContentTypes( Session session, QualifiedContentTypeNames qualifiedContentTypeNames );
}
