package com.enonic.wem.core.schema;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.GetChildSchemas;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.command.CommandHandler;


public class GetChildSchemasHandler
    extends CommandHandler<GetChildSchemas>
{

    @Override
    public void handle()
        throws Exception
    {
        // Get child contentTypes
        final ContentTypeName contentTypeName = ContentTypeName.from( command.getParentKey().getLocalName() );
        ContentTypes contentTypes = context.getClient().execute( Commands.contentType().getChildren().parentName( contentTypeName ) );

        // RelationshipTypes are not nested so there cannot be child ones
        // Mixins are not nested so there cannot be child ones

        command.setResult( Schemas.from( contentTypes ) );
    }

}
