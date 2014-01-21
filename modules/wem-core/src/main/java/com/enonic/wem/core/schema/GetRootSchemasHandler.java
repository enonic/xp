package com.enonic.wem.core.schema;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.GetRootSchemas;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.command.CommandHandler;


public class GetRootSchemasHandler
    extends CommandHandler<GetRootSchemas>
{
    @Override
    public void handle()
        throws Exception
    {
        final List<Schema> schemas = Lists.newArrayList();

        // ContentTypes are nested so query just the root ones
        final ContentTypes contentTypes = context.getClient().execute( Commands.contentType().get().roots() );
        if ( contentTypes.isNotEmpty() )
        {
            schemas.addAll( contentTypes.getList() );
        }

        // RelationshipTypes are not nested so adding all to root
        final RelationshipTypes relationshipTypes = context.getClient().execute( Commands.relationshipType().get().all() );
        if ( relationshipTypes.isNotEmpty() )
        {
            schemas.addAll( relationshipTypes.getList() );
        }

        // Mixins are not nested so adding all to root
        final Mixins mixins = context.getClient().execute( Commands.mixin().get().all() );
        if ( mixins.isNotEmpty() )
        {
            schemas.addAll( mixins.getList() );
        }

        command.setResult( Schemas.from( schemas ) );
    }

}
