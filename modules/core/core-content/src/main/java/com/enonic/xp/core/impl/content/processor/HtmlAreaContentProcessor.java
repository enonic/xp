package com.enonic.xp.core.impl.content.processor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.core.impl.content.ContentConfig;
import com.enonic.xp.core.internal.processor.InternalHtmlSanitizer;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.DescriptorBasedComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.Regions;
import com.enonic.xp.region.TextComponent;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

import static com.google.common.base.Strings.nullToEmpty;

@Component(configurationPid = "com.enonic.xp.content")
public class HtmlAreaContentProcessor
    implements ContentProcessor
{
    private static final String CONTENT_TYPE = "content";

    private static final String MEDIA_TYPE = "media";

    private static final String IMAGE_TYPE = "image";

    private static final String DOWNLOAD_MODE = "download";

    private static final String INLINE_MODE = "inline";

    private static final int ID_INDEX = 8;

    private static final Pattern CONTENT_PATTERN = Pattern.compile(
        "(<(\\w+)[^>]+?(href|src)=(\"((" + CONTENT_TYPE + "|" + MEDIA_TYPE + "|" + IMAGE_TYPE + ")://(?:(" + DOWNLOAD_MODE + "|" +
            INLINE_MODE + ")/)?([0-9a-z-/]+)(\\?[^\"]+)?)\"))", Pattern.MULTILINE | Pattern.UNIX_LINES );

    private final boolean sanitizingEnabled;

    private ContentTypeService contentTypeService;

    private XDataService xDataService;

    private SiteService siteService;

    private PageDescriptorService pageDescriptorService;

    private PartDescriptorService partDescriptorService;

    private LayoutDescriptorService layoutDescriptorService;

    @Activate
    public HtmlAreaContentProcessor( final ContentConfig config )
    {
        sanitizingEnabled = config.htmlarea_sanitizing_enabled();
    }

    @Override
    public boolean supports( final ContentTypeName contentType )
    {
        return true;
    }

    @Override
    public ProcessCreateResult processCreate( final ProcessCreateParams params )
    {
        final CreateContentParams createContentParams = params.getCreateContentParams();

        final ContentIds.Builder processedIds = ContentIds.create();

        final ContentType contentType = contentTypeService.getByName( GetContentTypeParams.from( createContentParams.getType() ) );

        processContentData( createContentParams.getData(), contentType, processedIds );
        processExtraData( createContentParams.getExtraDatas(), processedIds );
        processPageData( createContentParams.getPage(), processedIds );
        if ( createContentParams.getType().isSite() )
        {
            processSiteConfigData( SiteConfigsDataSerializer.fromData( createContentParams.getData().getRoot() ), processedIds );
        }

        return new ProcessCreateResult( createContentParams, processedIds.build() );
    }

    @Override
    public ProcessUpdateResult processUpdate( final ProcessUpdateParams params )
    {
        final ContentIds.Builder processedIds = ContentIds.create();
        final Content inputContent = params.getContent();
        final ContentType contentType = contentTypeService.getByName( GetContentTypeParams.from( inputContent.getType() ) );

        processContentData( inputContent.getData(), contentType, processedIds );
        processExtraData( inputContent.getAllExtraData(), processedIds );
        final Page page = processPageData( inputContent.getPage(), processedIds );

        if ( inputContent instanceof Site site )
        {
            processSiteConfigData( SiteConfigsDataSerializer.fromData( site.getData().getRoot() ), processedIds );
        }

        return new ProcessUpdateResult( Content.create( inputContent ).page( page ).processedReferences( processedIds.build() ).build()  );
    }

    private void processSiteConfigData( final SiteConfigs siteConfigs, final ContentIds.Builder processedIds )
    {
        for ( SiteConfig siteConfig : siteConfigs )
        {
            final SiteDescriptor siteDescriptor = siteService.getDescriptor( siteConfig.getApplicationKey() );

            if ( siteDescriptor == null )
            {
                continue;
            }

            final Collection<Property> properties = getProperties( siteConfig.getConfig(), siteDescriptor.getForm() );
            processDataTree( properties, processedIds );
        }
    }

    private Page processPageData( final Page page, final ContentIds.Builder processedIds )
    {
        if ( page == null )
        {
            return null;
        }

        if ( page.hasDescriptor() )
        {
            final PageDescriptor pageDescriptor = pageDescriptorService.getByKey( page.getDescriptor() );

            final Collection<Property> properties = getProperties( page.getConfig(), pageDescriptor.getConfig() );
            processDataTree( properties, processedIds );
        }

        if ( page.hasRegions() )
        {
            final Regions regions = processRegionsData( page.getRegions(), processedIds );
            return Page.create( page ).regions( regions ).build();
        }

        return page.copy();
    }

    private Regions processRegionsData( final Regions regions, final ContentIds.Builder processedIds )
    {
        final Regions.Builder result = Regions.create();
        for ( final Region region : regions )
        {
            final Region processedRegion = this.processRegionData( region, processedIds );

            result.add( processedRegion );
        }

        return result.build();
    }

    private Region processRegionData( final Region region, final ContentIds.Builder processedIds )
    {
        final List<com.enonic.xp.region.Component> processedComponents = region.getComponents().stream().map( component -> {
            if ( component instanceof TextComponent )
            {
                final String processedText = processString( ( (TextComponent) component ).getText(), processedIds );
                return TextComponent.create( (TextComponent) component ).text( processedText ).build();
            }

            if ( component instanceof DescriptorBasedComponent )
            {
                processComponent( (DescriptorBasedComponent) component, processedIds );
            }

            return component;
        } ).collect( Collectors.toList() );

        final Region.Builder processedRegion = Region.create( region );

        for ( int i = 0; i < processedComponents.size(); i++ )
        {
            processedRegion.set( i, processedComponents.get( i ) );
        }

        return processedRegion.build();
    }

    private void processExtraData( final ExtraDatas extraDatas, final ContentIds.Builder processedIds )
    {
        for ( ExtraData extraData : extraDatas )
        {
            final XData xData = xDataService.getByName( extraData.getName() );
            if ( xData != null )
            {
                processDataTree( getProperties( extraData.getData(), xData.getForm() ), processedIds );
            }
        }
    }

    private void processContentData( final PropertyTree contentData, final ContentType contentType, final ContentIds.Builder processedIds )
    {
        processDataTree( getProperties( contentData, contentType.getForm() ), processedIds );
    }

    private Collection<Property> getProperties( final PropertyTree data, final Iterable<FormItem> formItems )
    {
        if ( data == null || data.getTotalSize() == 0 )
        {
            return Collections.emptyList();
        }

        return getProperties( formItems, data ).stream().filter( Property::hasNotNullValue ).collect( Collectors.toList() );
    }

    private Set<Property> getProperties( final Iterable<FormItem> formItems, final PropertyTree data )
    {
        final HtmlAreaVisitor visitor = new HtmlAreaVisitor( data );
        visitor.traverse( formItems );

        return visitor.getProperties();
    }

    private void processDataTree( final Collection<Property> properties, final ContentIds.Builder processedIds )
    {
        for ( Property property : properties )
        {
            final String processedValue = processString( property.getString(), processedIds );

            property.setValue( ValueFactory.newString( processedValue ) );
        }
    }

    private void processComponent( final DescriptorBasedComponent component, final ContentIds.Builder processedIds )
    {
        if ( component.hasDescriptor() )
        {
            if ( component instanceof LayoutComponent )
            {
                final LayoutDescriptor layoutDescriptor = this.layoutDescriptorService.getByKey( component.getDescriptor() );

                if ( layoutDescriptor != null )
                {
                    processComponentDescriptor( component, layoutDescriptor, processedIds );
                }

                final LayoutComponent layoutComponent = (LayoutComponent) component;

                if ( layoutComponent.hasRegions() )
                {
                    processRegionsData( layoutComponent.getRegions(), processedIds );
                }
            }

            if ( component instanceof PartComponent )
            {
                final PartDescriptor partDescriptor = this.partDescriptorService.getByKey( component.getDescriptor() );

                if ( partDescriptor != null )
                {
                    processComponentDescriptor( component, partDescriptor, processedIds );
                }
            }
        }
    }

    private String processString( final String value, final ContentIds.Builder processedIds )
    {
        if ( nullToEmpty( value ).isBlank() )
        {
            return null;
        }

        final String processedValue = sanitizingEnabled ? InternalHtmlSanitizer.richText().sanitize( value ) : value;

        final Matcher contentMatcher = CONTENT_PATTERN.matcher( processedValue );

        while ( contentMatcher.find() )
        {
            if ( contentMatcher.groupCount() >= ID_INDEX )
            {
                final String id = contentMatcher.group( ID_INDEX );
                final ContentId contentId = ContentId.from( id );
                processedIds.add( contentId );
            }
        }

        return processedValue;
    }

    private void processComponentDescriptor( final DescriptorBasedComponent component, final ComponentDescriptor componentDescriptor,
                                             final ContentIds.Builder processedIds )
    {
        final Collection<Property> properties = getProperties( component.getConfig(), componentDescriptor.getConfig() );
        processDataTree( properties, processedIds );
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    @Reference
    public void setXDataService( final XDataService xDataService )
    {
        this.xDataService = xDataService;
    }

    @Reference
    public void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
    }

    @Reference
    public void setPageDescriptorService( final PageDescriptorService pageDescriptorService )
    {
        this.pageDescriptorService = pageDescriptorService;
    }

    @Reference
    public void setPartDescriptorService( final PartDescriptorService partDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
    }

    @Reference
    public void setLayoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
    }
}
