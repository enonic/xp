package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.google.common.io.Files;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.Media;
import com.enonic.xp.portal.impl.MediaHashResolver;
import com.enonic.xp.project.ProjectName;

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

        final String hash = MediaHashResolver.resolveImageHash( media );
        final String scale = resolveScale( params.getScale() );
        final String name = resolveName( media, params.getFormat() );

        final StringBuilder url = new StringBuilder();

//        appendPart( url, "media:image" );
        appendPart( url, project + ( ContentConstants.BRANCH_MASTER.equals( branch ) ? "" : ":" + branch ) );
        appendPart( url, media.getId() + ( hash != null ? ":" + hash : "" ) );
        appendPart( url, scale );
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
}
