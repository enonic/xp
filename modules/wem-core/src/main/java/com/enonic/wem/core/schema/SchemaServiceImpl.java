package com.enonic.wem.core.schema;

import javax.inject.Inject;

import com.enonic.wem.api.schema.SchemaTypesParams;
import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.schema.SchemaService;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;

public final class SchemaServiceImpl
    implements SchemaService
{
    @Inject
    protected ContentTypeService contentTypeService;

    @Inject
    protected MixinService mixinService;

    @Inject
    protected RelationshipTypeService relationshipTypeService;

    @Override
    public Schemas getRoot()
    {
        return new GetRootSchemasCommand().contentTypeService( this.contentTypeService ).mixinService(
            this.mixinService ).relationshipTypeService( this.relationshipTypeService ).execute();
    }

    @Override
    public Schemas getChildren( final SchemaKey parent )
    {
        return new GetChildSchemasCommand().parentKey( parent ).contentTypeService( this.contentTypeService ).execute();
    }

    @Override
    public Schemas getTypes( final SchemaTypesParams params )
    {
        return new GetSchemasCommand().contentTypeService( this.contentTypeService ).mixinService(
            this.mixinService ).relationshipTypeService( this.relationshipTypeService ).params( params ).execute();
    }
}
