package com.enonic.wem.core.schema;

import javax.inject.Inject;

import com.enonic.wem.api.command.schema.GetChildSchemas;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetChildContentTypesParams;
import com.enonic.wem.core.command.CommandHandler;


public class GetChildSchemasHandler
    extends CommandHandler<GetChildSchemas>
{
    private ContentTypeService contentTypeService;

    @Override
    public void handle()
        throws Exception
    {
        // Get child contentTypes
        final ContentTypeName contentTypeName = ContentTypeName.from( command.getParentKey().getLocalName() );
        final GetChildContentTypesParams params = new GetChildContentTypesParams().parentName( contentTypeName );
        final ContentTypes contentTypes = contentTypeService.getChildren( params );

        // RelationshipTypes are not nested so there cannot be child ones
        // Mixins are not nested so there cannot be child ones

        command.setResult( Schemas.from( contentTypes ) );
    }

    @Inject
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }
}
