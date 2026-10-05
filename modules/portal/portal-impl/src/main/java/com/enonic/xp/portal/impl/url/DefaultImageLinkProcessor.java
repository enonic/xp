package com.enonic.xp.portal.impl.url;

import java.util.List;
import java.util.Optional;
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
import com.enonic.xp.portal.impl.ImageScaling;
import com.enonic.xp.portal.url.PortalUrlGeneratorService;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.portal.url.UrlGeneratorParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.style.ImageStyle;

import static java.util.Objects.requireNonNullElse;

final class DefaultImageLinkProcessor
{
    private static final Pattern ASPECT_RATIO_PATTEN = Pattern.compile( "^(?<horizontalProportion>\\d+):(?<verticalProportion>\\d+)$" );

    private static final String IMAGE_SCALE = "width(768)";

    private static final int DEFAULT_WIDTH = 768;

    ContentService contentService;

    PortalUrlGeneratorService portalUrlGeneratorService;

    Supplier<String> baseUrlSupplier;

    ProcessHtmlParams params;

    HtmlElement element;

    ImageStyle imageStyle;

    String id;

    String scaleFromQueryString;

    void process()
    {

        final Supplier<ProjectName> projectNameSupplier = Suppliers.memoize(
            () -> ContentProjectResolver.create().setPreferSiteRequest( params.getBaseUrl() == null ).build().resolve() );

        final Supplier<Branch> branchSupplier =
            Suppliers.memoize( () -> ContentBranchResolver.create().setPreferSiteRequest( params.getBaseUrl() == null ).build().resolve() );

        final Supplier<Media> imageSupplier = IdentifiedSupplier.of( id, Suppliers.memoize( () -> {
            final Content content = ContextBuilder.copyOf( ContextAccessor.current() )
                .repositoryId( projectNameSupplier.get().getRepoId() )
                .branch( branchSupplier.get() )
                .build()
                .callWith( () -> contentService.getById( ContentId.from( id ) ) );

            if ( content instanceof Media media && ( media.getType().isImageMedia() || media.getType().isVectorMedia() ) )
            {
                return media;
            }
            throw new IllegalStateException( String.format( "Content with id '%s' is not an image", id ) );
        } ) );

        // an image served as stored takes no filter
        final Supplier<String> queryParamsStrategy = () -> {
            final DefaultQueryParamsSupplier queryParams = new DefaultQueryParamsSupplier();
            if ( isScalable( imageSupplier ) )
            {
                Optional.ofNullable( imageStyle ).map( ImageStyle::getFilter ).ifPresent( filter -> queryParams.param( "filter", filter ) );
            }
            return queryParams.get();
        };

        final String imageUrl = imageUrl( baseUrlSupplier, imageSupplier, projectNameSupplier, branchSupplier, queryParamsStrategy, null );

        element.setAttribute( element.hasAttribute( "href" ) ? "href" : "src", imageUrl );

        if ( "img".equals( element.getTagName() ) && isScalable( imageSupplier ) )
        {
            final List<Integer> imageWidths = params.getImageWidths();
            // sizes goes with the width descriptors of a srcset, so both are written, or neither
            if ( imageWidths != null && !imageWidths.isEmpty() )
            {
                final String srcsetValues = imageWidths.stream().map( imageWidth -> {
                    final String scaledImageUrl =
                        imageUrl( baseUrlSupplier, imageSupplier, projectNameSupplier, branchSupplier, queryParamsStrategy, imageWidth );

                    return scaledImageUrl + " " + imageWidth + "w";
                } ).collect( Collectors.joining( "," ) );

                element.setAttribute( "srcset", srcsetValues );

                final String imageSizes = params.getImageSizes();
                if ( imageSizes != null && !imageSizes.trim().isEmpty() )
                {
                    element.setAttribute( "sizes", imageSizes );
                }
            }
        }
    }

    /**
     * @return whether the image resolves and the image API scales it
     */
    private static boolean isScalable( final Supplier<Media> imageSupplier )
    {
        try
        {
            return ImageScaling.isScalable( imageSupplier.get() );
        }
        catch ( RuntimeException e )
        {
            return false;
        }
    }

    private String imageUrl( final Supplier<String> baseUrlSupplier, final Supplier<Media> imageSupplier,
                             final Supplier<ProjectName> projectNameSupplier, final Supplier<Branch> branchSupplier,
                             final Supplier<String> queryParamsStrategy, final Integer imageWidth )
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
        return portalUrlGeneratorService.generateUrl( imageUrl );
    }

    private String getScale( final ImageStyle imageStyle, final Integer expectedWidth )
    {
        return scale( imageStyle, scaleFromQueryString, expectedWidth );
    }

    /**
     * @return the scale of an image in processed HTML: a block of the aspect ratio of the style, or of the one the link
     * carries, at the width - {@code 768} unless given - and otherwise that width
     */
    static String scale( final ImageStyle imageStyle, final String scaleFromQueryString, final Integer expectedWidth )
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

            final int width = requireNonNullElse( expectedWidth, DEFAULT_WIDTH );
            final int height = width / Integer.parseInt( horizontalProportion ) * Integer.parseInt( verticalProportion );

            return "block(" + width + "," + height + ")";
        }

        return expectedWidth != null ? "width(" + expectedWidth + ")" : IMAGE_SCALE;
    }
}
