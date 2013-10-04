package com.enonic.wem.core.schema;

import java.util.List;

import javax.inject.Inject;
import javax.jcr.Session;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import com.enonic.wem.api.command.schema.SchemaTypes;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaKind;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;


public final class GetSchemasHandler
    extends CommandHandler<SchemaTypes>
{
    private ContentTypeDao contentTypeDao;

    private MixinDao mixinDao;

    private RelationshipTypeDao relationshipTypeDao;

    @Override
    public void handle( final SchemaTypes command )
        throws Exception
    {
        final Session session = context.getJcrSession();

        final List<Schema> schemaList = Lists.newArrayList();
        if ( command.isIncludeType( SchemaKind.CONTENT_TYPE ) )
        {
            final ContentTypes contentTypes = contentTypeDao.selectAll( session );
            Iterables.addAll( schemaList, contentTypes );
        }

        if ( command.isIncludeType( SchemaKind.MIXIN ) )
        {
            final Mixins mixins = mixinDao.selectAll( session );
            Iterables.addAll( schemaList, mixins );
        }

        if ( command.isIncludeType( SchemaKind.RELATIONSHIP_TYPE ) )
        {
            final RelationshipTypes relationshipTypes = relationshipTypeDao.selectAll( session );
            Iterables.addAll( schemaList, relationshipTypes );
        }

        final Schemas schemas = Schemas.from( schemaList );

        command.setResult( schemas );
    }

    @Inject
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }

    @Inject
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }

    @Inject
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }
}
