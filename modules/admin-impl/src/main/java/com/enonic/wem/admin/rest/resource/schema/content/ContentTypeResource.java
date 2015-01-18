package com.enonic.wem.admin.rest.resource.schema.content;

import java.awt.image.BufferedImage;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.admin.AdminResource;
import com.enonic.wem.admin.json.schema.content.ContentTypeJson;
import com.enonic.wem.admin.json.schema.content.ContentTypeSummaryListJson;
import com.enonic.wem.admin.rest.exception.NotFoundWebException;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageHelper;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetAllContentTypesParams;
import com.enonic.wem.api.schema.content.GetContentTypeParams;

@Path(ResourceConstants.REST_ROOT + "schema/content")
@Produces("application/json")
@RolesAllowed("admin-login")
public final class ContentTypeResource
    implements AdminResource
{
    private static final SchemaImageHelper helper = new SchemaImageHelper();

    private ContentTypeService contentTypeService;

    private ContentTypeIconUrlResolver contentTypeIconUrlResolver;

    private ContentTypeIconResolver contentTypeIconResolver;

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
        return new ContentTypeJson( contentType, this.contentTypeIconUrlResolver );
    }

    @GET
    @Path("all")
    public ContentTypeSummaryListJson all(
        @DefaultValue("false") @QueryParam("mixinReferencesToFormItems") final boolean mixinReferencesToFormItems )
    {
        return list( mixinReferencesToFormItems );
    }

    @GET
    @Path("list")
    public ContentTypeSummaryListJson list(
        @DefaultValue("false") @QueryParam("mixinReferencesToFormItems") final boolean mixinReferencesToFormItems )
    {

        final GetAllContentTypesParams getAll = new GetAllContentTypesParams().mixinReferencesToFormItems( mixinReferencesToFormItems );
        final ContentTypes contentTypes = contentTypeService.getAll( getAll );

        return new ContentTypeSummaryListJson( contentTypes, this.contentTypeIconUrlResolver );
    }

    @GET
    @Path("byModule")
    public ContentTypeSummaryListJson getByModule( @QueryParam("moduleKey") final String moduleKey )
    {
        final ContentTypes contentTypes = contentTypeService.getByModule( ModuleKey.from( moduleKey ) );
        return new ContentTypeSummaryListJson( contentTypes, this.contentTypeIconUrlResolver );
    }

    @GET
    @Path("icon/{contentTypeName}")
    @Produces("image/*")
    public Response getIcon( @PathParam("contentTypeName") final String contentTypeNameAsString,
                             @QueryParam("size") @DefaultValue("128") final int size, @QueryParam("hash") final String hash )
        throws Exception
    {
        final ContentTypeName contentTypeName = ContentTypeName.from( contentTypeNameAsString );
        final Icon icon = this.contentTypeIconResolver.resolveIcon( contentTypeName );
        if ( icon == null )
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        final BufferedImage image = helper.resizeImage( icon.asInputStream(), size );
        final Response.ResponseBuilder responseBuilder = Response.ok( image, icon.getMimeType() );

        if ( StringUtils.isNotEmpty( hash ) )
        {
            applyMaxAge( Integer.MAX_VALUE, responseBuilder );
        }
        return responseBuilder.build();
    }

    private void applyMaxAge( int maxAge, final Response.ResponseBuilder responseBuilder )
    {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge( maxAge );
        responseBuilder.cacheControl( cacheControl );
    }

    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
        this.contentTypeIconResolver = new ContentTypeIconResolver( contentTypeService );
        this.contentTypeIconUrlResolver = new ContentTypeIconUrlResolver( this.contentTypeIconResolver );
    }
}
