package com.enonic.wem.core.content.dao;

import javax.jcr.Session;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeNames;
import com.enonic.wem.api.content.type.ContentTypes;

public interface ContentTypeDao
{
    public void createContentType( Session session, ContentType contentType );

    public ContentTypes retrieveContentTypes( Session session, ContentTypeNames contentTypeNames );

    public void updateContentType( Session session, ContentType contentType );

    public int deleteContentType( Session session, ContentTypeNames contentTypeNames );
}
