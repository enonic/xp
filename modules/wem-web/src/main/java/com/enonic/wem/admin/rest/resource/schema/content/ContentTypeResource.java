package com.enonic.wem.admin.rest.resource.schema.content;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.commons.lang.BooleanUtils;
import org.elasticsearch.common.Required;

import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.schema.content.model.AbstractContentTypeJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeConfigRpcJson;
import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeJson;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;

@Path("schema/content")
public class ContentTypeResource
    extends AbstractResource
{
    public static final String FORMAT_XML = "XML";

    public static final String FORMAT_JSON = "JSON";

    @GET
    public AbstractContentTypeJson get( @FormParam("format") @Required final String format,
                                        @FormParam("contentType") @Required final String contentType,
                                        @FormParam("mixinReferencesToFormItems") final Boolean reference )
    {
        final QualifiedContentTypeName qualifiedName = new QualifiedContentTypeName( contentType );
        final GetContentTypes getContentTypes = Commands.contentType().get().qualifiedNames( QualifiedContentTypeNames.from( qualifiedName ) );
        getContentTypes.mixinReferencesToFormItems( BooleanUtils.isTrue( reference ) );
        final ContentTypes result = client.execute( getContentTypes );

        if ( !result.isEmpty() )
        {
            if ( format.equalsIgnoreCase( FORMAT_JSON ) )
            {
                return new ContentTypeJson( result.first() );
            }
            else if ( format.equalsIgnoreCase( FORMAT_XML ) )
            {
                return new ContentTypeConfigRpcJson( result.first() );
            }
        }
        throw new NotFoundException( String.format( "ContentType [%s] was not found", qualifiedName ) );
    }



    // list() @GET
    // find() @GET
    // generateName() @GET
    // validate() @POST
    // delete() @POST
    // create() @POST
    // update() @POST
}
