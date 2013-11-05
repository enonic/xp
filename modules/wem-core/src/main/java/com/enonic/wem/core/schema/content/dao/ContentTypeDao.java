package com.enonic.wem.core.schema.content.dao;

import javax.jcr.Session;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.jcr.JcrConstants;

public interface ContentTypeDao
{
    static final String CONTENT_TYPES_NODE = "contentTypes";

    static final String CONTENT_TYPES_PATH = JcrConstants.ROOT_NODE + "/" + CONTENT_TYPES_NODE + "/";

    void create( ContentType contentType, Session session );

    void update( ContentType contentType, Session session );

    void delete( ContentTypeName contentTypeName, Session session );

    ContentTypes selectAll( Session session );

    ContentTypes select( ContentTypeNames contentTypeNames, Session session );

    ContentType select( ContentTypeName contentTypeName, Session session );
}
