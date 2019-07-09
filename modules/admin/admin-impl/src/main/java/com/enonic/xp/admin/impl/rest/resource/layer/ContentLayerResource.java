package com.enonic.xp.admin.impl.rest.resource.layer;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.content.ResolvedImage;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.layer.ContentLayer;
import com.enonic.xp.layer.ContentLayerName;
import com.enonic.xp.layer.ContentLayerService;
import com.enonic.xp.layer.CreateContentLayerParams;
import com.enonic.xp.layer.GetContentLayerIconResult;
import com.enonic.xp.layer.UpdateContentLayerParams;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.util.Exceptions;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

@Path(ResourceConstants.REST_ROOT + "layer")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleKeys.ADMIN_LOGIN_ID, RoleKeys.ADMIN_ID})
@Component(immediate = true, property = "group=admin")
public class ContentLayerResource
    implements JaxRsComponent
{
    private ContentLayerService contentLayerService;

    @GET
    @Path("list")
    public List<ContentLayerJson> list()
    {
        return contentLayerService.list().
            stream().
            map( contentLayer -> new ContentLayerJson( contentLayer ) ).
            collect( Collectors.toList() );
    }

    @GET
    @Path("get")
    public ContentLayerJson get( @QueryParam("name") final String name )
    {
        final ContentLayer contentLayer = contentLayerService.get( name == null ? null : ContentLayerName.from( name ) );
        return contentLayer == null ? null : new ContentLayerJson( contentLayer );
    }

    @POST
    @Path("create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ContentLayerJson create( final MultipartForm form )
    {
        final String name = form.getAsString( "name" );
        final String parentName = form.getAsString( "parentName" );
        final String displayName = form.getAsString( "displayName" );
        final String description = form.getAsString( "description" );
        final String language = form.getAsString( "language" );
        final String iconName = form.getAsString( "iconName" );
        final MultipartItem iconItem = form.get( "icon" );

        final CreateContentLayerParams params = CreateContentLayerParams.create().
            name( name == null ? null : ContentLayerName.from( name ) ).
            parentName( parentName == null ? null : ContentLayerName.from( parentName ) ).
            displayName( displayName ).
            description( description ).
            language( language == null ? null : Locale.forLanguageTag( language ) ).
            iconName( iconName ).
            iconMimeType( iconItem == null ? null : iconItem.getContentType().toString() ).
            iconByteSource( iconItem == null ? null : iconItem.getBytes() ).
            build();

        final ContentLayer contentLayer = contentLayerService.create( params );
        return new ContentLayerJson( contentLayer );
    }

    @POST
    @Path("update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ContentLayerJson update( final MultipartForm form )
    {
        final String name = form.getAsString( "name" );
        final String displayName = form.getAsString( "displayName" );
        final String description = form.getAsString( "description" );
        final String language = form.getAsString( "language" );
        final String iconName = form.getAsString( "iconName" );
        final MultipartItem iconItem = form.get( "icon" );

        final UpdateContentLayerParams params = UpdateContentLayerParams.create().
            name( name == null ? null : ContentLayerName.from( name ) ).
            displayName( displayName ).
            description( description ).
            language( language == null ? null : Locale.forLanguageTag( language ) ).
            iconName( iconName ).
            iconMimeType( iconItem == null ? null : iconItem.getContentType().toString() ).
            iconByteSource( iconItem == null ? null : iconItem.getBytes() ).
            build();

        final ContentLayer contentLayer = contentLayerService.update( params );
        return new ContentLayerJson( contentLayer );
    }

    @GET
    @Path("icon")
    public Response getIcon( @QueryParam("name") final String name )
    {

        final GetContentLayerIconResult result = contentLayerService.getIcon( ContentLayerName.from( name ) );
        if ( result.isFound() )
        {
            try
            {
                final ResolvedImage resolvedImage = new ResolvedImage( result.getByteSource().read(), result.getMimeType() );
                if (resolvedImage.isOK()) {
                    return resolvedImage.toResponse();
                }
            }
            catch ( IOException e )
            {
                throw Exceptions.unchecked( e );
            }
        }
        throw new WebApplicationException( Response.Status.NOT_FOUND );
    }

    @Reference
    public void setContentLayerService( final ContentLayerService contentLayerService )
    {
        this.contentLayerService = contentLayerService;
    }
}
