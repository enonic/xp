package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeProvider;
import com.enonic.wem.api.schema.content.ContentTypeRegistry;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.schema.BaseRegistry;

public final class ContentTypeRegistryImpl
    extends BaseRegistry<ContentTypeProvider, ContentType, ContentTypes, ContentTypeName>
    implements ContentTypeRegistry
{
    public ContentTypeRegistryImpl()
    {
        super( ContentTypeProvider.class, ContentType::getName );
    }

    public ContentType getContentType( final ContentTypeName name )
    {
        return super.getItemByName( name );
    }

    public ContentTypes getContentTypesByModule( final ModuleKey moduleKey )
    {
        final ContentTypes contentTypes = super.getItemsByModule( moduleKey );
        return contentTypes == null ? ContentTypes.empty() : contentTypes;
    }

    public ContentTypes getAllContentTypes()
    {
        return ContentTypes.from( super.getAllItems() );
    }

}
