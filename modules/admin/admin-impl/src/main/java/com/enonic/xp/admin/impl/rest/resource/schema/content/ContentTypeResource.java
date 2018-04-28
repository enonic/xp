package com.enonic.xp.admin.impl.rest.resource.schema.content;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.json.schema.content.ContentTypeJson;
import com.enonic.xp.admin.impl.json.schema.content.ContentTypeSummaryJson;
import com.enonic.xp.admin.impl.json.schema.content.ContentTypeSummaryListJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.schema.SchemaImageHelper;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetAllContentTypesParams;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.support.AbstractImmutableEntityList;

@Path(ResourceConstants.REST_ROOT + "schema/content")
@Produces("application/json")
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true, property = "group=admin")
public final class ContentTypeResource
    implements JaxRsComponent
{
    private static final SchemaImageHelper HELPER = new SchemaImageHelper();

    private ContentTypeService contentTypeService;

    private ContentTypeIconUrlResolver contentTypeIconUrlResolver;

    private ContentTypeIconResolver contentTypeIconResolver;

    private LocaleService localeService;

    private ContentService contentService;

    @GET
    public ContentTypeJson get( @QueryParam("name") final String nameAsString,
                                @DefaultValue("false") @QueryParam("inlineMixinsToFormItems") final boolean inlineMixinsToFormItems )
    {
        final ContentTypeName name = ContentTypeName.from( nameAsString );
        final GetContentTypeParams getContentTypes = GetContentTypeParams.from( name ).
            inlineMixinsToFormItems( inlineMixinsToFormItems );

        final ContentType contentType = contentTypeService.getByName( getContentTypes );
        if ( contentType == null )
        {
            throw new WebApplicationException( String.format( "ContentType [%s] not found", name ), Response.Status.NOT_FOUND );
        }
        final LocaleMessageResolver localeMessageResolver =
            new LocaleMessageResolver( this.localeService, contentType.getName().getApplicationKey() );
        return new ContentTypeJson( contentType, this.contentTypeIconUrlResolver, localeMessageResolver );
    }

    @GET
    @Path("all")
    public ContentTypeSummaryListJson all()
    {
        return list( true );
    }

    @GET
    @Path("list")
    public ContentTypeSummaryListJson list(
        @DefaultValue("false") @QueryParam("inlineMixinsToFormItems") final boolean inlineMixinsToFormItems )
    {

        final GetAllContentTypesParams getAll = new GetAllContentTypesParams().inlineMixinsToFormItems( inlineMixinsToFormItems );
        final ContentTypes contentTypes = contentTypeService.getAll( getAll );

        ImmutableList.Builder<ContentTypeSummaryJson> summariesJsonBuilder = new ImmutableList.Builder();

        contentTypes.forEach( contentType -> {
            summariesJsonBuilder.add( new ContentTypeSummaryJson( contentType, this.contentTypeIconUrlResolver,
                                                                  new LocaleMessageResolver( localeService,
                                                                                             contentType.getName().getApplicationKey() ) ) );
        } );

        return new ContentTypeSummaryListJson( summariesJsonBuilder.build() );
    }

    @GET
    @Path("getMimeTypes")
    public Collection<String> getMimeTypes( @QueryParam("typeNames") final String typeNames )
    {
        return contentTypeService.getMimeTypes( ContentTypeNames.from( typeNames.split( "," ) ) );
    }

    @GET
    @Path("byContent")
    public ContentTypeSummaryListJson byContent( @QueryParam("contentId") final String content )
    {
        final ContentId contentId = ContentId.from( content );
        final Site site = contentService.getNearestSite( contentId );

        final ContentTypes contentTypes;
        if ( site != null )
        {
            final List<ContentType> types = site.getSiteConfigs().stream().
                map( SiteConfig::getApplicationKey ).
                map( ( appKey ) -> contentTypeService.getByApplication( appKey ) ).
                flatMap( AbstractImmutableEntityList::stream ).
                collect( Collectors.toList() );
            contentTypes = ContentTypes.from( types );
        }
        else
        {
            contentTypes = ContentTypes.empty();
        }

        ImmutableList.Builder<ContentTypeSummaryJson> summariesJsonBuilder = new ImmutableList.Builder();

        contentTypes.forEach( type -> {
            summariesJsonBuilder.add( new ContentTypeSummaryJson( type, this.contentTypeIconUrlResolver,
                                                                  new LocaleMessageResolver( localeService,
                                                                                             type.getName().getApplicationKey() ) ) );
        } );

        return new ContentTypeSummaryListJson( summariesJsonBuilder.build() );
    }

    @GET
    @Path("byApplication")
    public ContentTypeSummaryListJson getByApplication( @QueryParam("applicationKey") final String applicationKey )
    {
        final ContentTypes contentTypes = contentTypeService.getByApplication( ApplicationKey.from( applicationKey ) );

        final LocaleMessageResolver localeMessageResolver =
            new LocaleMessageResolver( this.localeService, ApplicationKey.from( applicationKey ) );
        ;
        return new ContentTypeSummaryListJson( contentTypes, this.contentTypeIconUrlResolver, localeMessageResolver );
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

        final Object image = HELPER.isSvg( icon ) ? icon.toByteArray() : HELPER.resizeImage( icon.asInputStream(), size );
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

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
        this.contentTypeIconResolver = new ContentTypeIconResolver( contentTypeService );
        this.contentTypeIconUrlResolver = new ContentTypeIconUrlResolver( this.contentTypeIconResolver );
    }

    @Reference
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
