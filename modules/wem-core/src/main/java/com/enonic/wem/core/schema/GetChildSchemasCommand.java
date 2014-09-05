package com.enonic.wem.core.schema;

import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetChildContentTypesParams;

final class GetChildSchemasCommand
{
    private SchemaKey parentKey;

    private ContentTypeService contentTypeService;

    public Schemas execute()
    {
        // Get child contentTypes
        final ContentTypeName contentTypeName = (ContentTypeName) this.parentKey.getName();
        final GetChildContentTypesParams params = new GetChildContentTypesParams().parentName( contentTypeName );
        final ContentTypes contentTypes = this.contentTypeService.getChildren( params );

        // RelationshipTypes are not nested so there cannot be child ones
        // Mixins are not nested so there cannot be child ones

        return Schemas.from( contentTypes );
    }

    public GetChildSchemasCommand parentKey( final SchemaKey parentKey )
    {
        this.parentKey = parentKey;
        return this;
    }

    public GetChildSchemasCommand contentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
        return this;
    }
}
