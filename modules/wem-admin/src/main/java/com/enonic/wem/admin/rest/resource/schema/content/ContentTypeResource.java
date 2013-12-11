package com.enonic.wem.admin.rest.resource.schema.content;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.admin.json.schema.content.ContentTypeConfigJson;
import com.enonic.wem.admin.json.schema.content.ContentTypeJson;
import com.enonic.wem.admin.json.schema.content.ContentTypeSummaryListJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.schema.json.CreateOrUpdateSchemaJsonResult;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteJson;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteParams;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.DeleteContentType;
import com.enonic.wem.api.command.schema.content.DeleteContentTypeResult;
import com.enonic.wem.api.command.schema.content.GetAllContentTypes;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.command.schema.content.UpdateContentType;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.support.serializer.XmlParsingException;
import com.enonic.wem.core.schema.content.serializer.ContentTypeXmlSerializer;

import static com.enonic.wem.api.command.Commands.contentType;

@Path("schema/content")
@Produces("application/json")
public class ContentTypeResource
    extends AbstractResource
{
    @GET
    public ContentTypeJson get( @QueryParam("name") final String nameAsString,
                                @QueryParam("mixinReferencesToFormItems") final Boolean mixinReferencesToFormItems )
    {
        final ContentTypeName name = ContentTypeName.from( nameAsString );
        final GetContentTypes getContentTypes = Commands.contentType().get().
            byNames().contentTypeNames( ContentTypeNames.from( name ) ).
            mixinReferencesToFormItems( mixinReferencesToFormItems );

        final ContentTypes contentTypes = client.execute( getContentTypes );
        if ( contentTypes.isEmpty() )
        {
            throw new NotFoundException( String.format( "ContentTypes [%s] not found", name ) );
        }
        return new ContentTypeJson( contentTypes.first() );
    }

    @GET
    @Path("config")
    public ContentTypeConfigJson getConfig( @QueryParam("name") final String nameAsString )
    {
        final ContentTypeName name = ContentTypeName.from( nameAsString );
        final GetContentTypes getContentTypes = Commands.contentType().
            get().
            byNames().contentTypeNames( ContentTypeNames.from( name ) ).
            mixinReferencesToFormItems( false );

        final ContentTypes contentTypes = client.execute( getContentTypes );

        if ( contentTypes.isEmpty() )
        {
            throw new NotFoundException( String.format( "ContentTypes [%s] not found", name ) );
        }

        return new ContentTypeConfigJson( contentTypes.first() );
    }

    @GET
    @Path("all")
    public ContentTypeSummaryListJson all( @QueryParam("mixinReferencesToFormItems") final Boolean mixinReferencesToFormItems )
    {
        final GetAllContentTypes getAll = Commands.contentType().get().all();
        getAll.mixinReferencesToFormItems( mixinReferencesToFormItems );
        final ContentTypes contentTypes = client.execute( getAll );
        return new ContentTypeSummaryListJson( contentTypes );
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public SchemaDeleteJson delete( SchemaDeleteParams params )
    {
        final ContentTypeNames contentTypeNames = ContentTypeNames.from( params.getNames().toArray( new String[0] ) );

        final SchemaDeleteJson deletionResult = new SchemaDeleteJson();
        for ( ContentTypeName contentTypeName : contentTypeNames )
        {
            final DeleteContentType deleteContentType = Commands.contentType().delete().name( contentTypeName );
            final DeleteContentTypeResult result = client.execute( deleteContentType );
            switch ( result )
            {
                case SUCCESS:
                    deletionResult.success( contentTypeName );
                    break;

                case NOT_FOUND:
                    deletionResult.failure( contentTypeName,
                                            String.format( "ContentType [%s] was not found", contentTypeName.toString() ) );

                    break;

                case UNABLE_TO_DELETE:
                    deletionResult.failure( contentTypeName,
                                            String.format( "Unable to delete ContentType [%s]", contentTypeName.toString() ) );
                    break;
            }
        }

        return deletionResult;
    }

    @POST
    @Path("create")
    public CreateOrUpdateSchemaJsonResult create( ContentTypeCreateJson json )
    {
        try
        {
            final ContentType created = client.execute( json.getCreateContentType() );
            return CreateOrUpdateSchemaJsonResult.result( new ContentTypeJson( created ) );

        }
        catch ( Exception e )
        {
            return CreateOrUpdateSchemaJsonResult.error( e.getMessage() );
        }
    }

    @POST
    @Path("update")
    public CreateOrUpdateSchemaJsonResult update( final ContentTypeUpdateJson json )
    {
        try
        {
            final UpdateContentType updateContentType = json.getUpdateContentType();
            client.execute( updateContentType );
            final ContentType persistedContentType =
                client.execute( Commands.contentType().get().byName().contentTypeName( json.getName() ) );

            return CreateOrUpdateSchemaJsonResult.result( new ContentTypeJson( persistedContentType ) );
        }
        catch ( Exception e )
        {
            return CreateOrUpdateSchemaJsonResult.error( e.getMessage() );
        }
    }


    @POST
    @Path("validate")
    public ValidateContentTypeJson validate( @FormParam("contentType") final String contentTypeXml )
    {
        final ContentType contentType;
        try
        {
            contentType = new ContentTypeXmlSerializer().toContentType( contentTypeXml );
        }
        catch ( XmlParsingException e )
        {
            throw new WebApplicationException( e );
        }

        final ContentTypeValidationResult validationResult = client.execute( contentType().validate().contentType( contentType ) );

        return new ValidateContentTypeJson( validationResult, contentType );
    }

}
