package com.enonic.xp.admin.impl.rest.resource.schema.content;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationWildcardMatcher;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigs;

@Component(service = FilterByContentResolver.class)
public class FilterByContentResolver
{
    private static final List<ContentTypeName> DEFAULT_CONTENT_TYPE_NAMES =
        List.of( ContentTypeName.folder(), ContentTypeName.site(), ContentTypeName.shortcut() );

    private static final List<ContentTypeName> DEFAULT_SITE_CONTENT_TYPE_NAMES =
        List.of( ContentTypeName.folder(), ContentTypeName.site(), ContentTypeName.shortcut(), ContentTypeName.templateFolder() );

    private ContentTypeService contentTypeService;

    private ContentService contentService;

    private LayoutDescriptorService layoutDescriptorService;

    private PartDescriptorService partDescriptorService;

    private PageDescriptorService pageDescriptorService;

    public Stream<ContentType> contentTypes( final ContentId contentId )
    {
        final Content content;
        if ( contentId != null )
        {
            content = this.contentService.getById( contentId );
        }
        else
        {
            return mapToContentTypes( DEFAULT_CONTENT_TYPE_NAMES );
        }

        if ( content.getType().isTemplateFolder() )
        {
            return mapToContentTypes( List.of( ContentTypeName.pageTemplate() ) );
        }
        else
        {
            final ContentType contentType = this.contentTypeService.getByName( GetContentTypeParams.from( content.getType() ) );
            if ( !contentType.allowChildContent() )
            {
                return Stream.empty();
            }

            final Stream<ContentType> defaultContentTypes =
                mapToContentTypes( content.getType().isSite() ? DEFAULT_SITE_CONTENT_TYPE_NAMES : DEFAULT_CONTENT_TYPE_NAMES );

            final ApplicationKeys siteApps = getNearestSiteApps( contentId );

            final Stream<ContentType> siteContentTypes = siteApps.stream()
                .map( contentTypeService::getByApplication )
                .flatMap( ContentTypes::stream )
                .filter( Predicate.not( ContentType::isAbstract ) )
                .filter( type -> type.getSchemaConfig().getValue( "allowNewContent", Boolean.class, Boolean.TRUE ) );

            final List<String> allowChildContentType = contentType.getAllowChildContentType();

            final Predicate<ContentTypeName> allowChildContentTypePredicate =
                allowContentTypeFilter( contentType.getName().getApplicationKey(), allowChildContentType );

            return Stream.concat( defaultContentTypes, siteContentTypes )
                .filter( type -> allowChildContentTypePredicate.test( type.getName() ) );
        }
    }

    public Stream<LayoutDescriptor> layouts( final ContentId contentId )
    {
        return filteredComponentsStream( contentId, layoutDescriptorService::getByApplications );
    }

    public Stream<PartDescriptor> parts( final ContentId contentId )
    {
        return filteredComponentsStream( contentId, partDescriptorService::getByApplications );
    }

    public Stream<PageDescriptor> pages( final ContentId contentId )
    {
        return filteredComponentsStream( contentId, pageDescriptorService::getByApplications );
    }

    private <T extends ComponentDescriptor> Stream<T> filteredComponentsStream( final ContentId contentId,
                                                                                final Function<ApplicationKeys, Iterable<T>> supplier )
    {
        final Content content = this.contentService.getById( contentId );

        final ContentTypeName contentTypeName = content.getType();

        final ApplicationKeys siteApps = getNearestSiteApps( contentId );

        return StreamSupport.stream( supplier.apply( siteApps ).spliterator(), false )
            .filter( descriptor -> isAllowedOnContentType( descriptor, contentTypeName ) );
    }

    private ApplicationKeys getNearestSiteApps( final ContentId contentId )
    {
        return Optional.ofNullable( contentService.getNearestSite( contentId ) )
            .map( Site::getSiteConfigs )
            .map( SiteConfigs::getApplicationKeys )
            .map( ApplicationKeys::from )
            .orElse( ApplicationKeys.empty() );
    }

    private Stream<ContentType> mapToContentTypes( final List<ContentTypeName> contentTypeNames )
    {
        return contentTypeNames.stream().map( GetContentTypeParams::from ).map( contentTypeService::getByName );
    }

    private boolean isAllowedOnContentType( final ComponentDescriptor descriptor, ContentTypeName contentTypeName )
    {
        final List<String> allowOnContentType = readConfigValues( descriptor.getSchemaConfig().getProperties( "allowOnContentType" ) );

        return allowContentTypeFilter( descriptor.getKey().getApplicationKey(), allowOnContentType ).test( contentTypeName );
    }

    private Predicate<ContentTypeName> allowContentTypeFilter( final ApplicationKey applicationKey, final List<String> wildcards )
    {
        final ApplicationWildcardMatcher<ContentTypeName> wildcardMatcher =
            new ApplicationWildcardMatcher<>( applicationKey, ContentTypeName::toString );
        return wildcards.stream().map( wildcardMatcher::createPredicate ).reduce( Predicate::or ).orElse( s -> true );
    }

    private static List<String> readConfigValues( final Set<InputTypeProperty> config )
    {
        return config.stream().map( InputTypeProperty::getValue ).collect( Collectors.toList() );
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    @Reference
    public void setLayoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
    }

    @Reference
    public void setPartDescriptorService( final PartDescriptorService partDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
    }

    @Reference
    public void setPageDescriptorService( final PageDescriptorService pageDescriptorService )
    {
        this.pageDescriptorService = pageDescriptorService;
    }
}
