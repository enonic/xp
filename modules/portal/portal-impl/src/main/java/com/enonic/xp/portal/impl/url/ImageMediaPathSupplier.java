package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import com.google.common.io.Files;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.Media;
import com.enonic.xp.portal.impl.MediaHashResolver;
import com.enonic.xp.portal.impl.HmacService;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.image.ScaleParamsParser;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.style.ImageStyle;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;

final class ImageMediaPathSupplier
    implements Supplier<String>
{
    private final Supplier<Media> mediaSupplier;

    private final Supplier<ProjectName> projectNameSupplier;

    private final Supplier<Branch> branchSupplier;

    private final String scale;

    private final String format;

    private final Supplier<ImageStyle> styleSupplier;

    private final String styleKey;

    private final HmacService hmacService;

    private ImageMediaPathSupplier( final Builder builder )
    {
        this.scale = requireNonNull( builder.scale );
        this.mediaSupplier = builder.mediaSupplier;
        this.projectNameSupplier = builder.projectNameSupplier;
        this.branchSupplier = builder.branchSupplier;
        this.format = builder.format;
        this.styleSupplier = builder.styleSupplier;
        this.styleKey = builder.styleKey;
        this.hmacService = builder.hmacService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public String get()
    {
        final MediaPathParts parts = parts();

        final StringBuilder url = new StringBuilder();

        appendPart( url, parts.context() );
        appendPart( url, parts.idWithHash() );
        appendPart( url, parts.scale() );
        appendPart( url, parts.name() );

        return url.toString();
    }

    MediaPathParts parts()
    {
        final Media media = requireNonNull( mediaSupplier.get() );
        final ProjectName project = requireNonNull( projectNameSupplier.get() );
        final Branch branch = requireNonNull( branchSupplier.get() );

        final String context = project + ( ContentConstants.BRANCH_MASTER.equals( branch ) ? "" : ":" + branch );

        final ImageStyle style = styleSupplier.get();
        final String resolvedScale = resolveScale( scale );
        final ScaleParams scaleParams = style == null ? null : new ScaleParamsParser().parse( resolvedScale );
        if ( style != null )
        {
            requireNonNull( scaleParams, "Image scale is required" ).withAspectRatio( style.getAspectRatio() );
        }
        return new MediaPathParts( context, media.getId().toString(),
                                   MediaHashResolver.resolveStyledImageHash( MediaHashResolver.resolveImageHash( media ), style, scaleParams, hmacService ),
                                   styleKey == null ? resolvedScale : resolvedScale + "~" + DescriptorKey.from( styleKey ),
                                   resolveName( media, format ) );
    }

    private String resolveName( final Content media, final String format )
    {
        final String name = media.getName().toString();

        if ( format != null )
        {
            final String extension = Files.getFileExtension( name );
            if ( isNullOrEmpty( extension ) || !format.equals( extension ) )
            {
                return name + "." + format;
            }
        }
        return name;
    }

    private String resolveScale( final String scale )
    {
        if ( scale.indexOf( '~' ) >= 0 )
        {
            throw new IllegalArgumentException( "Specify image style separately from scale" );
        }
        return scale.replaceAll( "\\s", "" ).replaceAll( "[(,]", "-" ).replace( ")", "" );
    }

    static class Builder
    {
        private Supplier<Media> mediaSupplier;

        private Supplier<ProjectName> projectNameSupplier;

        private Supplier<Branch> branchSupplier;

        private String scale;

        private String format;

        private Supplier<ImageStyle> styleSupplier = () -> null;

        private String styleKey;

        private HmacService hmacService;

        public Builder setHmacService( final HmacService hmacService )
        {
            this.hmacService = hmacService;
            return this;
        }

        public Builder setStyle( final String styleKey, final Supplier<ImageStyle> styleSupplier )
        {
            this.styleSupplier = styleSupplier;
            this.styleKey = styleKey;
            return this;
        }

        public Builder setMedia( final Supplier<Media> mediaSupplier )
        {
            this.mediaSupplier = mediaSupplier;
            return this;
        }

        public Builder setProjectName( final Supplier<ProjectName> projectNameSupplier )
        {
            this.projectNameSupplier = projectNameSupplier;
            return this;
        }

        public Builder setBranch( final Supplier<Branch> branchSupplier )
        {
            this.branchSupplier = branchSupplier;
            return this;
        }

        public Builder setScale( final String scale )
        {
            this.scale = scale;
            return this;
        }

        public Builder setFormat( final String format )
        {
            this.format = format;
            return this;
        }

        public ImageMediaPathSupplier build()
        {
            return new ImageMediaPathSupplier( this );
        }
    }
}
