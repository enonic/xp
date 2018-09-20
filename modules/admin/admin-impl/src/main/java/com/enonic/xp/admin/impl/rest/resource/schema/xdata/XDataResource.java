package com.enonic.xp.admin.impl.rest.resource.schema.xdata;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.admin.impl.json.schema.xdata.XDataJson;
import com.enonic.xp.admin.impl.json.schema.xdata.XDataListJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.ContentTypeNameWildcardResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.MixinIconResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.MixinIconUrlResolver;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetAllContentTypesParams;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.schema.xdata.XDatas;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.XDataMappings;

import static java.util.stream.Collectors.toList;

@Path(ResourceConstants.REST_ROOT + "schema/xdata")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleKeys.ADMIN_LOGIN_ID, RoleKeys.ADMIN_ID})
@Component(immediate = true, property = "group=admin")
public final class XDataResource
    implements JaxRsComponent
{
    private XDataService xDataService;

    private ContentService contentService;

    private SiteService siteService;

    private ContentTypeService contentTypeService;

    private LocaleService localeService;

    private MixinIconUrlResolver mixinIconUrlResolver;

    @GET
    @Path("getContentXData")
    public XDataListJson getContentXData( @QueryParam("contentId") final String id )
    {
        final ContentId contentId = ContentId.from( id );
        final Content content = this.contentService.getById( contentId );

        final XDataListJson result = new XDataListJson();

        final Map<XData, Boolean> resultXData = Maps.newLinkedHashMap();

        getContentTypeXData( content ).forEach( xData -> resultXData.put( xData, false ) );

        getSiteXData( content ).entrySet().forEach( entry -> {
            if ( !resultXData.containsKey( entry.getKey() ) )
            {
                resultXData.put( entry.getKey(), entry.getValue() );
            }
        } );

        result.addXDatas( createXDataListJson( resultXData ) );

        return result;
    }

    @GET
    @Path("getApplicationXDataForContentType")
    public XDataListJson getApplicationXDataForContentType( @QueryParam("contentTypeName") final String contentTypeName,
                                                            @QueryParam("applicationKey") final String key )
    {
        final ContentTypeName contentType = ContentTypeName.from( contentTypeName );
        final ApplicationKey applicationKey = ApplicationKey.from( key );

        final XDataListJson result = new XDataListJson();

        final SiteDescriptor siteDescriptor = siteService.getDescriptor( ApplicationKey.from( key ) );

        final ContentTypes contentTypes = contentTypeService.getAll( new GetAllContentTypesParams().inlineMixinsToFormItems( false ) );

        final Map<XData, Boolean> siteXData =
            this.getXDatasByContentType( siteDescriptor.getXDataMappings(), ContentTypeName.from( contentTypeName ), contentTypes );

        final XDatas applicationXData =
            XDatas.from( this.filterXDataByContentType( this.xDataService.getByApplication( applicationKey ), contentType, contentTypes ).
                stream().filter( externalMixin -> !siteXData.keySet().contains( externalMixin ) ).iterator() );

        result.addXDatas( createXDataListJson( siteXData ) );
        result.addXDatas( createXDataListJson( applicationXData.stream().collect( Collectors.toMap( xdata -> xdata, xdata -> false ) ) ) );

        return result;
    }

    private List<XDataJson> createXDataListJson( final Map<XData, Boolean> xDatas )
    {
        return xDatas.keySet().stream().map(
            xData -> XDataJson.create().setXData( xData ).setIconUrlResolver( this.mixinIconUrlResolver ).setLocaleMessageResolver(
                new LocaleMessageResolver( localeService, xData.getName().getApplicationKey() ) ).setOptional(
                xDatas.get( xData ) ).build() ).collect( toList() );
    }

    private Map<XData, Boolean> getSiteXData( final Content content )
    {
        final Map<XData, Boolean> result = Maps.newHashMap();

        final Site nearestSite = this.contentService.getNearestSite( content.getId() );

        if ( nearestSite != null )
        {
            final List<ApplicationKey> applicationKeys =
                nearestSite.getSiteConfigs().stream().map( SiteConfig::getApplicationKey ).collect( toList() );

            final List<SiteDescriptor> siteDescriptors =
                applicationKeys.stream().map( applicationKey -> siteService.getDescriptor( applicationKey ) ).filter(
                    Objects::nonNull ).collect( toList() );

            final ContentTypes contentTypes = contentTypeService.getAll( new GetAllContentTypesParams().inlineMixinsToFormItems( false ) );

            siteDescriptors.forEach( siteDescriptor -> result.putAll(
                this.getXDatasByContentType( siteDescriptor.getXDataMappings(), content.getType(), contentTypes ) ) );

        }
        return result;
    }

    private Map<XData, Boolean> getXDatasByContentType( final XDataMappings xDataMappings, final ContentTypeName contentTypeName,
                                                        final ContentTypes contentTypes )
    {
        final Map<XData, Boolean> result = Maps.newHashMap();

        filterXDataMappingsByContentType( xDataMappings, contentTypeName, contentTypes ).forEach(
            xDataMapping -> result.put( this.xDataService.getByName( xDataMapping.getXDataName() ), xDataMapping.getOptional() ) );

        return result;
    }

    private Boolean isXDataAllowed( final XDataName xDataName, final String allowContentType, final ContentTypeName contentTypeName,
                                    final ContentTypes contentTypes )
    {
        final ContentTypeNameWildcardResolver contentTypeNameWildcardResolver =
            new ContentTypeNameWildcardResolver( this.contentTypeService );

        final List<String> allowContentTypes =
            StringUtils.isNotBlank( allowContentType ) ? Collections.singletonList( allowContentType ) : Lists.newArrayList();

        if ( contentTypeNameWildcardResolver.anyTypeHasWildcard( allowContentTypes ) )
        {
            final ContentTypeNames validContentTypes = ContentTypeNames.from(
                contentTypeNameWildcardResolver.resolveWildcards( contentTypes, allowContentTypes, xDataName.getApplicationKey() ) );

            if ( validContentTypes.contains( contentTypeName ) )
            {
                return true;
            }
        }
        else if ( StringUtils.isNotBlank( allowContentType ) )
        {
            if ( contentTypeName.equals( ContentTypeName.from( allowContentType ) ) )
            {
                return true;
            }
        }
        else
        {
            return true;
        }

        return false;
    }

    private XDatas filterXDataByContentType( final XDatas xDatas, final ContentTypeName contentTypeName, final ContentTypes contentTypes )
    {
        final XDatas.Builder result = XDatas.create();

        xDatas.forEach( xData -> {
            if ( isXDataAllowed( xData.getName(), null, contentTypeName, contentTypes ) )
            {
                result.add( xData );
            }
        } );

        return result.build();
    }

    private XDataMappings filterXDataMappingsByContentType( final XDataMappings xDataMappings, final ContentTypeName contentTypeName,
                                                            final ContentTypes contentTypes )
    {
        final XDataMappings.Builder filteredXDatas = XDataMappings.create();

        xDataMappings.forEach( xDataMapping -> {

            if ( isXDataAllowed( xDataMapping.getXDataName(), xDataMapping.getAllowContentTypes(), contentTypeName, contentTypes ) )
            {
                filteredXDatas.add( xDataMapping );
            }
        } );

        return filteredXDatas.build();
    }

    private XDatas getContentTypeXData( final Content content )
    {
        final ContentType contentType = this.contentTypeService.getByName( GetContentTypeParams.from( content.getType() ) );

        return XDatas.from( this.xDataService.getByNames( contentType.getMetadata() ) );
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
        this.mixinIconUrlResolver = new MixinIconUrlResolver( new MixinIconResolver( mixinService ) );
    }
}


