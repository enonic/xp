package com.enonic.wem.core.content.schema.content.dao;

import javax.jcr.Session;

import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.ContentTypes;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
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

    ContentType select( QualifiedContentTypeName qualifiedContentTypeName, Session session );
}
