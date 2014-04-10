package com.enonic.wem.core.schema;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

final class GetRootSchemasCommand
{
    private MixinService mixinService;

    private RelationshipTypeService relationshipTypeService;

    private ContentTypeService contentTypeService;

    public Schemas execute()
    {
        final List<Schema> schemas = Lists.newArrayList();

        // ContentTypes are nested so query just the root ones
        final ContentTypes contentTypes = contentTypeService.getRoots();
        if ( contentTypes.isNotEmpty() )
        {
            schemas.addAll( contentTypes.getList() );
        }

        // RelationshipTypes are not nested so adding all to root
        final RelationshipTypes relationshipTypes = relationshipTypeService.getAll();
        if ( relationshipTypes.isNotEmpty() )
        {
            schemas.addAll( relationshipTypes.getList() );
        }

        // Mixins are not nested so adding all to root
        final Mixins mixins = mixinService.getAll();
        if ( mixins.isNotEmpty() )
        {
            schemas.addAll( mixins.getList() );
        }

        return Schemas.from( schemas );
    }

    public GetRootSchemasCommand contentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
        return this;
    }

    public GetRootSchemasCommand mixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
        return this;
    }

    public GetRootSchemasCommand relationshipTypeService( final RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
        return this;
    }
}
