package com.enonic.wem.core.schema.content.dao;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;

public interface ContentTypeDao
{
    ContentType createContentType( ContentType contentType );

    void updateContentType( ContentType contentType );

    ContentTypes getAllContentTypes();

    ContentType.Builder getContentType( ContentTypeName contentTypeName );

    boolean deleteContentType( ContentTypeName contentTypeName );
}
