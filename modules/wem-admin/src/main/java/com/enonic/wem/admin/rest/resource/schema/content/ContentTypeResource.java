package com.enonic.wem.admin.rest.resource.schema.content;

import javax.inject.Inject;
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

import com.enonic.wem.admin.json.icon.IconJson;
import com.enonic.wem.admin.json.schema.content.ContentTypeConfigJson;
import com.enonic.wem.admin.json.schema.content.ContentTypeJson;
import com.enonic.wem.admin.json.schema.content.ContentTypeSummaryListJson;
import com.enonic.wem.admin.rest.resource.schema.json.CreateOrUpdateSchemaJsonResult;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteJson;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteParams;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.schema.SchemaIcon;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypeNotFoundException;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.CreateContentTypeParams;
import com.enonic.wem.api.schema.content.DeleteContentTypeParams;
import com.enonic.wem.api.schema.content.GetAllContentTypesParams;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.GetContentTypesParams;
import com.enonic.wem.api.schema.content.UnableToDeleteContentTypeException;
import com.enonic.wem.api.schema.content.UpdateContentTypeParams;
import com.enonic.wem.api.schema.content.ValidateContentTypeParams;
import com.enonic.wem.api.schema.content.editor.ContentTypeEditor;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.support.serializer.XmlParsingException;
import com.enonic.wem.api.xml.XmlSerializers;

@Path("schema/content")
@Produces("application/json")
public class ContentTypeResource
{
    private ContentTypeService contentTypeService;

    private BlobService blobService;

    @GET
    public ContentTypeJson get( @QueryParam("name") final String nameAsString,
                                @QueryParam("mixinReferencesToFormItems") final Boolean mixinReferencesToFormItems )
    {
        final ContentTypeName name = ContentTypeName.from( nameAsString );
        final GetContentTypesParams getContentTypes = new GetContentTypesParams().
            contentTypeNames( ContentTypeNames.from( name ) ).
            mixinReferencesToFormItems( mixinReferencesToFormItems );

        final ContentTypes contentTypes = contentTypeService.getByNames( getContentTypes );
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
        final GetContentTypesParams getContentTypes = new GetContentTypesParams().
            contentTypeNames( ContentTypeNames.from( name ) ).
            mixinReferencesToFormItems( false );

        final ContentTypes contentTypes = contentTypeService.getByNames( getContentTypes );

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
        final GetAllContentTypesParams getAll = new GetAllContentTypesParams().mixinReferencesToFormItems( mixinReferencesToFormItems );
        final ContentTypes contentTypes = contentTypeService.getAll( getAll );

        return new ContentTypeSummaryListJson( contentTypes );
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public SchemaDeleteJson delete( SchemaDeleteParams params )
    {
        final ContentTypeNames contentTypeNames =
            ContentTypeNames.from( params.getNames().toArray( new String[params.getNames().size()] ) );

        final SchemaDeleteJson deletionResult = new SchemaDeleteJson();
        for ( ContentTypeName contentTypeName : contentTypeNames )
        {
            final DeleteContentTypeParams deleteContentType = new DeleteContentTypeParams().name( contentTypeName );
            try
            {
                contentTypeService.delete( deleteContentType );
                deletionResult.success( contentTypeName );
            }
            catch ( ContentTypeNotFoundException | UnableToDeleteContentTypeException e )
            {
                deletionResult.failure( contentTypeName, e.getMessage() );
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
            final CreateContentTypeParams createContentType = json.getCreateContentType();
            final SchemaIcon schemaIcon = getSchemaIcon( json.getIconJson() );
            if ( schemaIcon != null )
            {
                createContentType.schemaIcon( schemaIcon );
            }
            final ContentType created = contentTypeService.create( createContentType );
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
            final SchemaIcon schemaIcon = getSchemaIcon( json.getIconJson() );
            final ContentType contentTypeUpdate = json.getContentTypeUpdate();

            final ContentTypeEditor editor = new ContentTypeEditor()
            {
                @Override
                public ContentType edit( final ContentType toEdit )
                {
                    final ContentType.Builder builder = ContentType.newContentType( toEdit ).
                        name( contentTypeUpdate.getName() ).
                        displayName( contentTypeUpdate.getDisplayName() ).
                        description( contentTypeUpdate.getDescription() ).
                        superType( contentTypeUpdate.getSuperType() ).
                        setAbstract( contentTypeUpdate.isAbstract() ).
                        setFinal( contentTypeUpdate.isFinal() ).
                        contentDisplayNameScript( contentTypeUpdate.getContentDisplayNameScript() ).
                        form( contentTypeUpdate.form() );

                    if ( schemaIcon != null )
                    {
                        builder.icon( schemaIcon );
                    }

                    return builder.build();
                }
            };

            final UpdateContentTypeParams updateContentType = new UpdateContentTypeParams().
                contentTypeName( json.getContentTypeToUpdate() ).
                editor( editor );

            contentTypeService.update( updateContentType );
            final GetContentTypeParams params = new GetContentTypeParams().contentTypeName( json.getName() );
            final ContentType persistedContentType = contentTypeService.getByName( params );

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

        return new ValidateContentTypeJson( validationResult, contentType );
    }


    private SchemaIcon getSchemaIcon( final IconJson iconJson )
    {
        if ( iconJson != null )
        {
            final Blob blob = blobService.get( iconJson.getThumbnail().getBlobKey() );
            return blob == null ? null : SchemaIcon.from( blob.getStream(), iconJson.getMimeType() );
        }
        return null;
    }

    @Inject
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    @Inject
    public void setBlobService( final BlobService blobService )
    {
        this.blobService = blobService;
    }
}
