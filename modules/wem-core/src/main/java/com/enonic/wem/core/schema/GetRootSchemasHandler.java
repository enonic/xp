package com.enonic.wem.core.schema;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import com.enonic.wem.api.command.schema.GetRootSchemas;
import com.enonic.wem.api.command.schema.content.ContentTypeService;
import com.enonic.wem.api.command.schema.mixin.MixinService;
import com.enonic.wem.api.command.schema.relationship.RelationshipTypeService;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.command.CommandHandler;


public class GetRootSchemasHandler
    extends CommandHandler<GetRootSchemas>
{
    private MixinService mixinService;

    private RelationshipTypeService relationshipTypeService;

    private ContentTypeService contentTypeService;

    @Override
    public void handle()
        throws Exception
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

        command.setResult( Schemas.from( schemas ) );
    }

    @Inject
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
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
}
