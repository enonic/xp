package com.enonic.wem.api.schema.content;

import com.enonic.wem.api.module.ModuleKey;

public interface ContentTypeRegistry
{

    ContentType getContentType( ContentTypeName contentTypeName );

    ContentTypes getContentTypesByModule( ModuleKey moduleKey );

    ContentTypes getAllContentTypes();

}
