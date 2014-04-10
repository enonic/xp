package com.enonic.wem.core.schema;

import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import com.enonic.wem.api.schema.SchemaTypesParams;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaKind;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetAllContentTypesParams;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

final class GetSchemasCommand
{
    private SchemaTypesParams params;

    private MixinService mixinService;

    private RelationshipTypeService relationshipTypeService;

    private ContentTypeService contentTypeService;

    public Schemas execute()
    {
        final List<Schema> schemaList = Lists.newArrayList();
        if ( params.isIncludeType( SchemaKind.CONTENT_TYPE ) )
        {
            final ContentTypes contentTypes = contentTypeService.getAll( new GetAllContentTypesParams() );

            Iterables.addAll( schemaList, contentTypes );
        }

        if ( params.isIncludeType( SchemaKind.MIXIN ) )
        {
            final Mixins mixins = mixinService.getAll();
            Iterables.addAll( schemaList, mixins );
        }

        if ( params.isIncludeType( SchemaKind.RELATIONSHIP_TYPE ) )
        {
            final RelationshipTypes relationshipTypes = relationshipTypeService.getAll();
            Iterables.addAll( schemaList, relationshipTypes );
        }

        return Schemas.from( schemaList );
    }

    public GetSchemasCommand params( final SchemaTypesParams params )
    {
        this.params = params;
        return this;
    }

    public GetSchemasCommand contentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
        return this;
    }

    public GetSchemasCommand mixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
        return this;
    }

    public GetSchemasCommand relationshipTypeService( final RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
        return this;
    }
}
