package com.enonic.wem.core.content.type.dao;

import javax.jcr.Session;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.core.jcr.JcrConstants;

public interface ContentTypeDao
{
    static final String CONTENT_TYPES_NODE = "contentTypes";

    static final String CONTENT_TYPES_PATH = JcrConstants.ROOT_NODE + "/" + CONTENT_TYPES_NODE + "/";

    void create( ContentType contentType, Session session );

    void update( ContentType contentType, Session session );

    void delete( QualifiedContentTypeName qualifiedContentTypeName, Session session );

    ContentTypes selectAll( Session session );

    ContentTypes select( QualifiedContentTypeNames qualifiedContentTypeNames, Session session );
}
