package com.enonic.wem.admin.rpc.schema.content;


import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

import com.enonic.wem.admin.json.JsonErrorResult;
import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;


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
        final ContentTypeNames qualifiedNames =
            ContentTypeNames.from( context.param( "qualifiedNames" ).required().notBlank().asStringArray() );
        final GetContentTypes getContentTypes = Commands.contentType().get().qualifiedNames( qualifiedNames );
        getContentTypes.mixinReferencesToFormItems( context.param( "mixinReferencesToFormItems" ).asBoolean( false ) );
        final ContentTypes contentTypes = client.execute( getContentTypes );

        if ( qualifiedNames.getSize() == contentTypes.getSize() )
        {
            final String format = context.param( "format" ).required().asString();
            if ( format.equalsIgnoreCase( FORMAT_JSON ) )
            {
                context.setResult( new GetContentTypeJsonResult( contentTypes ) );
            }
            else if ( format.equalsIgnoreCase( FORMAT_XML ) )
            {
                context.setResult( new GetContentTypeConfigJsonResult( contentTypes ) );
            }
        }
        else
        {
            final ImmutableSet<ContentTypeName> found = contentTypes.getNames();

            final String[] notFound = FluentIterable.
                from( qualifiedNames ).
                filter( new Predicate<ContentTypeName>()
                {
                    public boolean apply( final ContentTypeName requested )
                    {
                        return !found.contains( requested );
                    }
                } ).
                transform( new Function<ContentTypeName, String>()
                {
                    public String apply( final ContentTypeName space )
                    {
                        return space.toString();
                    }
                } ).
                toArray( String.class );

            final String missing = Joiner.on( "," ).join( notFound );
            context.setResult( new JsonErrorResult( "ContentTypes [{0}] not found", missing ) );
        }
    }
}
