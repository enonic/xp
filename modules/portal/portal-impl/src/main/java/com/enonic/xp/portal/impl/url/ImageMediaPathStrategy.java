package com.enonic.xp.portal.impl.url;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.Media;
import com.enonic.xp.project.ProjectName;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendParams;
import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;
import static com.google.common.base.Strings.isNullOrEmpty;

final class ImageMediaPathStrategy
    implements PathStrategy
{
    private final ImageMediaPathStrategyParams params;

    ImageMediaPathStrategy( final ImageMediaPathStrategyParams params )
    {
        this.params = Objects.requireNonNull( params );
    }

    @Override
    public String generatePath()
    {
        final Media media = params.getMedia().get();
        final ProjectName project = params.getProjectName();
        final Branch branch = params.getBranch();

        final String hash = resolveHash( media );
        final String name = resolveName( media, params.getFormat() );
        final String scale = resolveScale( params.getScale() );

        final StringBuilder url = new StringBuilder();

        appendPart( url, "media" );
        appendPart( url, "image" );
        appendPart( url, project + ( ContentConstants.BRANCH_MASTER.equals( branch ) ? "" : ":" + branch ) );
        appendPart( url, media.getId() + ( hash != null ? ":" + hash : "" ) );
        appendPart( url, scale );
        appendPart( url, name );

        final Multimap<String, String> queryParams = resolveQueryParams();
        appendParams( url, queryParams.entries() );

        return url.toString();
    }

    private Multimap<String, String> resolveQueryParams()
    {
        final Multimap<String, String> queryParams = LinkedListMultimap.create();
        if ( this.params.getQuality() != null )
        {
            queryParams.put( "quality", this.params.getQuality().toString() );
        }
        if ( this.params.getBackground() != null )
        {
            queryParams.put( "background", this.params.getBackground() );
        }
        if ( this.params.getFilter() != null )
        {
            queryParams.put( "filter", this.params.getFilter() );
        }
        if ( params.getQueryParams() != null )
        {
            queryParams.putAll( params.getQueryParams() );
        }

        return queryParams;
    }

    private String resolveHash( final Media media )
    {
        final Attachment attachment = media.getMediaAttachment();

        if ( attachment.getSha512() == null )
        {
            return null;
        }

        return Hashing.sha1()
            .newHasher()
            .putString( attachment.getSha512().substring( 0, 32 ), StandardCharsets.UTF_8 )
            .putString( String.valueOf( media.getFocalPoint() ), StandardCharsets.UTF_8 )
            .putString( String.valueOf( media.getCropping() ), StandardCharsets.UTF_8 )
            .putString( String.valueOf( media.getOrientation() ), StandardCharsets.UTF_8 )
            .hash()
            .toString();
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
}
