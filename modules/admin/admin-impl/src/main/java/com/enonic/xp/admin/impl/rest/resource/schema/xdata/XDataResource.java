package com.enonic.xp.admin.impl.rest.resource.schema.xdata;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.json.schema.xdata.XDataJson;
import com.enonic.xp.admin.impl.json.schema.xdata.XDataListJson;
import com.enonic.xp.admin.impl.rest.AdminRestConfig;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.InlineMixinResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.MixinIconResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.MixinIconUrlResolver;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationWildcardMatcher;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.schema.xdata.XDatas;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.XDataMappings;

import static com.enonic.xp.admin.impl.rest.resource.ResourceConstants.CMS_PATH;
import static com.enonic.xp.admin.impl.rest.resource.ResourceConstants.REST_ROOT;
import static com.google.common.base.Strings.nullToEmpty;
import static java.util.stream.Collectors.toList;

@Path(REST_ROOT + "{content:(schema|" + CMS_PATH + "/schema)}/xdata")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleKeys.ADMIN_LOGIN_ID, RoleKeys.ADMIN_ID})
@Component(immediate = true, property = "group=admin", configurationPid = "com.enonic.xp.admin.rest")
public final class XDataResource
    implements JaxRsComponent
{
    private XDataService xDataService;

    private ContentService contentService;

    private SiteService siteService;

    private ContentTypeService contentTypeService;

    private LocaleService localeService;

    private MixinService mixinService;

    private MixinIconUrlResolver mixinIconUrlResolver;

    private ApplicationWildcardMatcher.Mode contentTypeParseMode;

    @Activate
    @Modified
    public void activate( final AdminRestConfig config )
    {
        contentTypeParseMode = ApplicationWildcardMatcher.Mode.valueOf( config.contentTypePatternMode() );
    }

    @GET
    @Path("getContentXData")
    public XDataListJson getContentXData( @QueryParam("contentId") final String id )
    {
        final ContentId contentId = ContentId.from( id );
        final Content content = this.contentService.getById( contentId );

        final XDataListJson result = new XDataListJson();

        final Map<XData, Boolean> resultXData = new LinkedHashMap<>();

        getContentTypeXData( content ).forEach( xData -> resultXData.putIfAbsent( xData, false ) );

        getSiteXData( content ).forEach( resultXData::putIfAbsent );

        result.addXDatas( createXDataListJson( resultXData ) );

        return result;
    }

    @GET
    @Path("getApplicationXDataForContentType")
    public XDataListJson getApplicationXDataForContentType( @QueryParam("contentTypeName") final String contentTypeName,
                                                            @QueryParam("applicationKey") final String key )
    {
        final XDataListJson result = new XDataListJson();

        final SiteDescriptor siteDescriptor = siteService.getDescriptor( ApplicationKey.from( key ) );

        final Map<XData, Boolean> siteXData =
            this.getXDatasByContentType( siteDescriptor.getXDataMappings(), ContentTypeName.from( contentTypeName ) );

        result.addXDatas( createXDataListJson( siteXData ) );

        return result;
    }

    private List<XDataJson> createXDataListJson( final Map<XData, Boolean> xDatas )
    {
        return xDatas.keySet()
            .stream()
            .map( xData -> XDataJson.create()
                .setXData( xData )
                .setIconUrlResolver( this.mixinIconUrlResolver )
                .setLocaleMessageResolver( new LocaleMessageResolver( localeService, xData.getName().getApplicationKey() ) )
                .setInlineMixinResolver( new InlineMixinResolver( mixinService ) )
                .setOptional( xDatas.get( xData ) )
                .build() )
            .distinct()
            .collect( toList() );
    }

    private Map<XData, Boolean> getSiteXData( final Content content )
    {
        final Map<XData, Boolean> result = new LinkedHashMap<>();

        final Site nearestSite = this.contentService.getNearestSite( content.getId() );

        if ( nearestSite != null )
        {
            final List<ApplicationKey> applicationKeys =
                nearestSite.getSiteConfigs().stream().map( SiteConfig::getApplicationKey ).collect( toList() );

            final List<SiteDescriptor> siteDescriptors = applicationKeys.stream()
                .map( applicationKey -> siteService.getDescriptor( applicationKey ) )
                .filter( Objects::nonNull )
                .collect( toList() );

            siteDescriptors.forEach(
                siteDescriptor -> result.putAll( this.getXDatasByContentType( siteDescriptor.getXDataMappings(), content.getType() ) ) );

        }
        return result;
    }

    private Map<XData, Boolean> getXDatasByContentType( final XDataMappings xDataMappings, final ContentTypeName contentTypeName )
    {
        final Map<XData, Boolean> result = new LinkedHashMap<>();

        xDataMappings.stream().filter( xDataMapping -> {
            final String wildcard = xDataMapping.getAllowContentTypes();
            final ApplicationKey applicationKey = xDataMapping.getXDataName().getApplicationKey();

            return nullToEmpty( wildcard ).isBlank() ||
                new ApplicationWildcardMatcher<>( applicationKey, ContentTypeName::toString, contentTypeParseMode ).matches( wildcard, contentTypeName );
        } ).forEach( xDataMapping -> {
            final XData xData = this.xDataService.getByName( xDataMapping.getXDataName() );
            if ( xData != null )
            {
                result.putIfAbsent( xData, xDataMapping.getOptional() );
            }
        } );

        return result;
    }

    private XDatas getContentTypeXData( final Content content )
    {
        final ContentType contentType = this.contentTypeService.getByName( GetContentTypeParams.from( content.getType() ) );

        return this.xDataService.getByNames( contentType.getXData() );
    }

    @Reference
    public void setXDataService( final XDataService xDataService )
    {
        this.xDataService = xDataService;
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


    @Reference
    public void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
        this.mixinIconUrlResolver = new MixinIconUrlResolver( new MixinIconResolver( mixinService ) );
    }
}


