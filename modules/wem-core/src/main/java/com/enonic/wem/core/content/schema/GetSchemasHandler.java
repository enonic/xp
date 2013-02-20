package com.enonic.wem.core.content.schema;

import java.util.List;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import com.enonic.wem.api.command.content.schema.SchemaTypes;
import com.enonic.wem.api.content.schema.Schema;
import com.enonic.wem.api.content.schema.SchemaKind;
import com.enonic.wem.api.content.schema.Schemas;
import com.enonic.wem.api.content.schema.content.ContentTypes;
import com.enonic.wem.api.content.schema.mixin.Mixins;
import com.enonic.wem.api.content.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.content.dao.ContentTypeDao;
import com.enonic.wem.core.content.schema.mixin.dao.MixinDao;
import com.enonic.wem.core.content.schema.relationship.dao.RelationshipTypeDao;

@Component
public final class GetSchemasHandler
    extends CommandHandler<SchemaTypes>
{
    private ContentTypeDao contentTypeDao;

    private MixinDao mixinDao;

    private RelationshipTypeDao relationshipTypeDao;

    public GetSchemasHandler()
    {
        super( SchemaTypes.class );
    }

    @Override
    public void handle( final CommandContext context, final SchemaTypes command )
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

    @Autowired
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }

    @Autowired
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }

    @Autowired
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }
}
