package com.enonic.wem.core.content.type.dao;

import javax.jcr.Session;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.content.type.ContentTypes;

public interface ContentTypeDao
{
    public void createContentType( Session session, ContentType contentType );

    public ContentTypes retrieveContentTypes( Session session, QualifiedContentTypeNames contentTypeNames );

    public ContentTypes retrieveAllContentTypes( Session session );

    public void updateContentType( Session session, ContentType contentType );

    public int deleteContentType( Session session, QualifiedContentTypeNames contentTypeNames );
}
