package com.enonic.wem.admin.rest.resource.schema.content;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import com.enonic.wem.admin.json.schema.content.ContentTypeConfigJson;
import com.enonic.wem.admin.json.schema.content.ContentTypeJson;
import com.enonic.wem.admin.json.schema.content.ContentTypeSummaryListJson;
import com.enonic.wem.admin.rest.exception.NotFoundWebException;
import com.enonic.wem.admin.rest.resource.schema.SchemaIconResolver;
import com.enonic.wem.admin.rest.resource.schema.SchemaIconUrlResolver;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetAllContentTypesParams;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.ValidateContentTypeParams;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.support.serializer.XmlParsingException;
import com.enonic.wem.api.xml.XmlSerializers;

@Path("schema/content")
@Produces("application/json")
public class ContentTypeResource
{
    private ContentTypeService contentTypeService;

    @GET
    public ContentTypeJson get( @QueryParam("name") final String nameAsString,
                                @QueryParam("mixinReferencesToFormItems") final Boolean mixinReferencesToFormItems )
    {
        final ContentTypeName name = ContentTypeName.from( nameAsString );
        final GetContentTypeParams getContentTypes = GetContentTypeParams.from( name ).
            mixinReferencesToFormItems( mixinReferencesToFormItems );

        final ContentType contentType = contentTypeService.getByName( getContentTypes );
        if ( contentType == null )
        {
            throw new NotFoundWebException( String.format( "ContentType [%s] not found", name ) );
        }
        return new ContentTypeJson( contentType, newSchemaIconUrlResolver() );
    }

    @GET
    @Path("config")
    public ContentTypeConfigJson getConfig( @QueryParam("name") final String nameAsString )
    {
        final ContentTypeName name = ContentTypeName.from( nameAsString );
        final GetContentTypeParams getContentTypes = GetContentTypeParams.from( name ).
            mixinReferencesToFormItems( false );

        final ContentType contentType = contentTypeService.getByName( getContentTypes );

        if ( contentType == null )
        {
            throw new NotFoundWebException( String.format( "ContentType [%s] not found", name ) );
        }

        return new ContentTypeConfigJson( contentType );
    }

    @GET
    @Path("all")
    public ContentTypeSummaryListJson all( @QueryParam("mixinReferencesToFormItems") final Boolean mixinReferencesToFormItems )
    {
        final GetAllContentTypesParams getAll = new GetAllContentTypesParams().mixinReferencesToFormItems( mixinReferencesToFormItems );
        final ContentTypes contentTypes = contentTypeService.getAll( getAll );

        return new ContentTypeSummaryListJson( contentTypes, newSchemaIconUrlResolver() );
    }

    @POST
    @Path("validate")
    public ValidateContentTypeJson validate( @FormParam("contentType") final String contentTypeXml )
    {
        final ContentType contentType;
        try
        {
            final ContentType.Builder builder = ContentType.newContentType();
            XmlSerializers.contentType().parse( contentTypeXml ).to( builder );
            contentType = builder.build();
        }
        catch ( XmlParsingException e )
        {
            throw new WebApplicationException( e );
        }

        final ValidateContentTypeParams params = new ValidateContentTypeParams().contentType( contentType );
        final ContentTypeValidationResult validationResult = contentTypeService.validate( params );

        return new ValidateContentTypeJson( validationResult, contentType, newSchemaIconUrlResolver() );
    }

    private SchemaIconUrlResolver newSchemaIconUrlResolver()
    {
        return new SchemaIconUrlResolver( new SchemaIconResolver( contentTypeService ) );
    }

    @Inject
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }
}
