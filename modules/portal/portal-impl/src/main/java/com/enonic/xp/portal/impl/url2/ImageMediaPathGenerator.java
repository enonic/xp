package com.enonic.xp.portal.impl.url2;

import java.util.Objects;
import java.util.function.Function;

import com.google.common.io.Files;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.Context;
import com.enonic.xp.project.ProjectName;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;
import static com.google.common.base.Strings.isNullOrEmpty;

public class ImageMediaPathGenerator
    implements PathGenerator
{
    private final UrlGeneratorContext context;

    private final ImageMediaUrlParams params;

    private final PathStrategy pathStrategy;

    private final Function<String, Media> mediaProvider;

    public ImageMediaPathGenerator( final UrlGeneratorContext context, final ImageMediaUrlParams params, final PathStrategy pathStrategy,
                                    Function<String, Media> mediaProvider )
    {
        this.params = params;
        this.context = context;
        this.pathStrategy = pathStrategy;
        this.mediaProvider = mediaProvider;
    }

    @Override
    public String generatePath()
    {
        final ProjectName project = Objects.requireNonNull( context.getProject() );
        final Branch branch = Objects.requireNonNullElse( context.getBranch(), ContentConstants.BRANCH_MASTER );

        final StringBuilder url = new StringBuilder();

        appendPart( url, pathStrategy.generatePathPrefix() );
        appendPart( url, "media" );
        appendPart( url, "image" );
        appendPart( url, project + ( ContentConstants.BRANCH_MASTER.equals( branch ) ? "" : ":" + branch ) );

        final Media media = mediaProvider.apply( null /*contentKey*/ );
        final String hash = resolveHash( media );
        final String name = resolveName( media, params.format );
        final String scale = resolveScale( params.scale );

        appendPart( url, media.getId() + ( hash != null ? ":" + hash : "" ) );
        appendPart( url, scale );
        appendPart( url, name );

        return url.toString();
    }

    private String resolveHash( final Media media )
    {
        final Attachment attachment = media.getMediaAttachment();
        return attachment.getSha512() != null ? attachment.getSha512().substring( 0, 32 ) : null;
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
        if ( scale == null )
        {
            throw new IllegalArgumentException( "Missing mandatory parameter 'scale' for image URL" );
        }

        return scale.replaceAll( "\\s", "" ).replaceAll( "[(,]", "-" ).replace( ")", "" );
    }

    @SuppressWarnings("unchecked")
    private <T> T getAttribute( final Context context, final String name )
    {
        return (T) context.getAttribute( name );
    }

    private <T> T getRequiredAttribute( final Context context, final String name )
    {
        return Objects.requireNonNull( getAttribute( context, name ) );
    }

    private <T> T getAttributeOrElse( final Context context, final String name, final T defaultValue )
    {
        return Objects.requireNonNullElse( getAttribute( context, name ), defaultValue );
    }
}
