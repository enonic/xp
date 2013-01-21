package com.enonic.wem.core.content.type.dao;

import javax.jcr.Session;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.core.jcr.JcrConstants;

public interface ContentTypeDao
{
    public static final String CONTENT_TYPES_NODE = "contentTypes";

    public static final String CONTENT_TYPES_PATH = JcrConstants.ROOT_NODE + "/" + CONTENT_TYPES_NODE + "/";

    public void createContentType( ContentType contentType, Session session );

    public void updateContentType( ContentType contentType, Session session );

    public void deleteContentType( QualifiedContentTypeName qualifiedContentTypeName, Session session );

    public ContentTypes retrieveAllContentTypes( Session session );

    public ContentTypes retrieveContentTypes( QualifiedContentTypeNames qualifiedContentTypeNames, Session session );
}
