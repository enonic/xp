package com.enonic.wem.core.schema;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import com.enonic.wem.api.command.schema.SchemaTypes;
import com.enonic.wem.api.command.schema.content.ContentTypeService;
import com.enonic.wem.api.command.schema.content.GetAllContentTypesParams;
import com.enonic.wem.api.command.schema.mixin.MixinService;
import com.enonic.wem.api.command.schema.relationship.RelationshipTypeService;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaKind;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.command.CommandHandler;


public final class GetSchemasHandler
    extends CommandHandler<SchemaTypes>
{
    private MixinService mixinService;

    private RelationshipTypeService relationshipTypeService;

    private ContentTypeService contentTypeService;

    @Override
    public void handle()
        throws Exception
    {
        final List<Schema> schemaList = Lists.newArrayList();
        if ( command.isIncludeType( SchemaKind.CONTENT_TYPE ) )
        {
            final ContentTypes contentTypes = contentTypeService.getAll( new GetAllContentTypesParams() );

            Iterables.addAll( schemaList, contentTypes );
        }

        if ( command.isIncludeType( SchemaKind.MIXIN ) )
        {
            final Mixins mixins = mixinService.getAll();
            Iterables.addAll( schemaList, mixins );
        }

        if ( command.isIncludeType( SchemaKind.RELATIONSHIP_TYPE ) )
        {
            final RelationshipTypes relationshipTypes = relationshipTypeService.getAll();
            Iterables.addAll( schemaList, relationshipTypes );
        }

        final Schemas schemas = Schemas.from( schemaList );

        command.setResult( schemas );
    }

    @Inject
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }

    @Inject
    public void setRelationshipTypeService( final RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    @Inject
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }
}
