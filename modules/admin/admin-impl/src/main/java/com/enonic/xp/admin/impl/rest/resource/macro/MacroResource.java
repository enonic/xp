package com.enonic.xp.admin.impl.rest.resource.macro;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.macro.json.MacrosJson;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.security.RoleKeys;

@Path(ResourceConstants.REST_ROOT + "macro")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public class MacroResource
    implements JaxRsComponent
{

    private MacroDescriptorService macroDescriptorService;

    private static final MacroImageHelper HELPER = new MacroImageHelper();

    private static final String DEFAULT_MIME_TYPE = "image/svg+xml";

    @GET
    @Path("list")
    public MacrosJson create()
    {
        return new MacrosJson( this.macroDescriptorService.getAll() );
    }

    @GET
    @Path("icon/{macroKey}")
    @Produces("image/*")
    public Response getIcon( @PathParam("macroKey") final String macroKeyStr, @QueryParam("size") @DefaultValue("128") final int size,
                             @QueryParam("hash") final String hash )
        throws Exception
    {
        final MacroKey macroKey = MacroKey.from( macroKeyStr );
        final Icon icon = null; //this.macroIconUrlResolver.resolveIcon( macroKey );

        final Response.ResponseBuilder responseBuilder;
        if ( icon == null )
        {
            final byte[] defaultMacroImage = HELPER.getDefaultMacroImage();
            responseBuilder = Response.ok( defaultMacroImage, DEFAULT_MIME_TYPE );
            applyMaxAge( Integer.MAX_VALUE, responseBuilder );
        }
        else
        {
            final Object image = HELPER.isSvg( icon ) ? icon.toByteArray() : HELPER.resizeImage( icon.asInputStream(), size );
            responseBuilder = Response.ok( image, icon.getMimeType() );
            if ( StringUtils.isNotEmpty( hash ) )
            {
                applyMaxAge( Integer.MAX_VALUE, responseBuilder );
            }
        }

        return responseBuilder.build();
    }

    private void applyMaxAge( int maxAge, final Response.ResponseBuilder responseBuilder )
    {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge( maxAge );
        responseBuilder.cacheControl( cacheControl );
    }

    @Reference
    public void setMacroDescriptorService( final MacroDescriptorService macroDescriptorService )
    {
        this.macroDescriptorService = macroDescriptorService;
    }
}
