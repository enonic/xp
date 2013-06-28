package com.enonic.wem.admin.rpc.schema.content;


import com.enonic.wem.admin.json.JsonErrorResult;
import com.enonic.wem.admin.json.rpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;


public final class GetContentTypeRpcHandler
    extends AbstractDataRpcHandler
{

    public static final String FORMAT_XML = "XML";

    public static final String FORMAT_JSON = "JSON";

    public GetContentTypeRpcHandler()
    {
        super( "contentType_get" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String format = context.param( "format" ).required().asString();
        final QualifiedContentTypeName qualifiedName = new QualifiedContentTypeName( context.param( "contentType" ).required().asString() );
        final GetContentTypes getContentTypes =
            Commands.contentType().get().qualifiedNames( QualifiedContentTypeNames.from( qualifiedName ) );
        getContentTypes.mixinReferencesToFormItems( context.param( "mixinReferencesToFormItems" ).asBoolean( false ) );
        final ContentTypes result = client.execute( getContentTypes );

        if ( !result.isEmpty() )
        {
            if ( format.equalsIgnoreCase( FORMAT_JSON ) )
            {
                context.setResult( new GetContentTypeRpcJsonResult( result.first() ) );
            }
            else if ( format.equalsIgnoreCase( FORMAT_XML ) )
            {
                context.setResult( new GetContentTypeConfigRpcJsonResult( result.first() ) );
            }
        }
        else
        {
            context.setResult( new JsonErrorResult( "ContentType [{0}] was not found", qualifiedName ) );
        }
    }
}
