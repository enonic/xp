package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;

public interface ContentTypeRegistry
{

    ContentType getContentType( ContentTypeName contentTypeName );

    ContentTypes getContentTypesByModule( ModuleKey moduleKey );

    ContentTypes getAllContentTypes();

}
