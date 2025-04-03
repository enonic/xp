package com.enonic.xp.portal.impl.url;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Suppliers;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.html.HtmlElement;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.portal.url.UrlGeneratorParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.style.ImageStyle;

final class DefaultImageLinkProcessor
{
    private static final Pattern ASPECT_RATIO_PATTEN = Pattern.compile( "^(?<horizontalProportion>\\d+):(?<verticalProportion>\\d+)$" );

    private static final String IMAGE_SCALE = "width(768)";

    private static final int DEFAULT_WIDTH = 768;

    ContentService contentService;

    PortalUrlService portalUrlService;

    ProcessHtmlParams params;

    HtmlElement element;

    ImageStyle imageStyle;

    String id;

    String scaleFromQueryString;

    void process()
    {
        final Supplier<String> baseUrlSupplier = Suppliers.memoize( () -> ApiUrlBaseUrlResolver.create()
            .setContentService( contentService )
            .setApplication( "media" )
            .setApi( "image" )
            .setBaseUrl( params.getBaseUrl() )
            .setUrlType( params.getType() )
            .build()
            .get() );

        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        queryParamsStrategy.putNotNull( "filter", imageStyle == null ? null : imageStyle.getFilter() );

        final Supplier<ProjectName> projectNameSupplier = Suppliers.memoize(
            () -> ContentProjectResolver.create().setPreferSiteRequest( params.getBaseUrl() == null ).build().resolve() );

        final Supplier<Branch> branchSupplier =
            Suppliers.memoize( () -> ContentBranchResolver.create().setPreferSiteRequest( params.getBaseUrl() == null ).build().resolve() );

        final Supplier<Media> imageSupplier = Suppliers.memoize( () -> {
            final Content content = ContextBuilder.copyOf( ContextAccessor.current() )
                .repositoryId( projectNameSupplier.get().getRepoId() )
                .branch( branchSupplier.get() )
                .build()
                .callWith( () -> contentService.getById( ContentId.from( id ) ) );

            if ( content instanceof Media media && media.isImage() )
            {
                return media;
            }
            throw new IllegalStateException( String.format( "Content with id '%s' is not an image", id ) );
        } );

        final String imageUrl = imageUrl( baseUrlSupplier, imageSupplier, projectNameSupplier, branchSupplier, queryParamsStrategy, null );

        element.setAttribute( element.hasAttribute( "href" ) ? "href" : "src", imageUrl );

        if ( "img".equals( element.getTagName() ) )
        {
            final List<Integer> imageWidths = params.getImageWidths();
            if ( imageWidths != null )
            {
                final String srcsetValues = imageWidths.stream().map( imageWidth -> {
                    final String scaledImageUrl =
                        imageUrl( baseUrlSupplier, imageSupplier, projectNameSupplier, branchSupplier, queryParamsStrategy, imageWidth );

                    return scaledImageUrl + " " + imageWidth + "w";
                } ).collect( Collectors.joining( "," ) );

                element.setAttribute( "srcset", srcsetValues );
            }

            final String imageSizes = params.getImageSizes();
            if ( imageSizes != null && !imageSizes.trim().isEmpty() )
            {
                element.setAttribute( "sizes", imageSizes );
            }
        }
    }

    private String imageUrl( final Supplier<String> baseUrlSupplier, final Supplier<Media> imageSupplier,
                             final Supplier<ProjectName> projectNameSupplier, final Supplier<Branch> branchSupplier,
                             final DefaultQueryParamsSupplier queryParamsStrategy, final Integer imageWidth )
    {
        final UrlGeneratorParams imageUrl = UrlGeneratorParams.create()
            .setBaseUrl( baseUrlSupplier )
            .setQueryString( queryParamsStrategy )
            .setPath( ImageMediaPathSupplier.create()
                          .setMedia( imageSupplier )
                          .setScale( getScale( imageStyle, imageWidth ) )
                          .setProjectName( projectNameSupplier )
                          .setBranch( branchSupplier )
                          .build() )
            .build();
        return portalUrlService.generateUrl( imageUrl );
    }

    private String getScale( final ImageStyle imageStyle, final Integer expectedWidth )
    {
        final String aspectRatio =
            imageStyle != null && imageStyle.getAspectRatio() != null ? imageStyle.getAspectRatio() : scaleFromQueryString;

        if ( aspectRatio != null )
        {
            final Matcher matcher = ASPECT_RATIO_PATTEN.matcher( aspectRatio );
            if ( !matcher.matches() )
            {
                throw new IllegalArgumentException( "Invalid aspect ratio: " + aspectRatio );
            }
            final String horizontalProportion = matcher.group( "horizontalProportion" );
            final String verticalProportion = matcher.group( "verticalProportion" );

            final int width = Objects.requireNonNullElse( expectedWidth, DEFAULT_WIDTH );
            final int height = width / Integer.parseInt( horizontalProportion ) * Integer.parseInt( verticalProportion );

            return "block(" + width + "," + height + ")";
        }

        return expectedWidth != null ? "width(" + expectedWidth + ")" : IMAGE_SCALE;
    }
}
