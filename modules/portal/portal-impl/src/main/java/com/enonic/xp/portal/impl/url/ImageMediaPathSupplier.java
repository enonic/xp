package com.enonic.xp.portal.impl.url;

import java.util.Objects;
import java.util.function.Supplier;

import com.google.common.io.Files;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.Media;
import com.enonic.xp.portal.impl.MediaHashResolver;
import com.enonic.xp.project.ProjectName;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;
import static com.google.common.base.Strings.isNullOrEmpty;

final class ImageMediaPathSupplier
    implements Supplier<String>
{
    private final Supplier<Media> mediaSupplier;

    private final Supplier<ProjectName> projectNameSupplier;

    private final Supplier<Branch> branchSupplier;

    private final String scale;

    private final String format;

    private ImageMediaPathSupplier( final Builder builder )
    {
        this.scale = Objects.requireNonNull( builder.scale );
        this.mediaSupplier = builder.mediaSupplier;
        this.projectNameSupplier = builder.projectNameSupplier;
        this.branchSupplier = builder.branchSupplier;
        this.format = builder.format;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public String get()
    {
        final Media media = Objects.requireNonNull( mediaSupplier.get() );
        final ProjectName project = Objects.requireNonNull( projectNameSupplier.get() );
        final Branch branch = Objects.requireNonNull( branchSupplier.get() );

        final String hash = MediaHashResolver.resolveImageHash( media );
        final String resolvedScale = resolveScale( scale );
        final String name = resolveName( media, format );

        final StringBuilder url = new StringBuilder();

        appendPart( url, project + ( ContentConstants.BRANCH_MASTER.equals( branch ) ? "" : ":" + branch ) );
        appendPart( url, media.getId() + ( hash != null ? ":" + hash : "" ) );
        appendPart( url, resolvedScale );
        appendPart( url, name );

        return url.toString();
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
        return scale.replaceAll( "\\s", "" ).replaceAll( "[(,]", "-" ).replace( ")", "" );
    }

    static class Builder
    {
        private Supplier<Media> mediaSupplier;

        private Supplier<ProjectName> projectNameSupplier;

        private Supplier<Branch> branchSupplier;

        private String scale;

        private String format;

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
