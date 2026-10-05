package com.enonic.xp.portal.impl.url;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.project.ProjectName;

/**
 * Looks up the content a media URL addresses, by a key in a project and branch. Shared by the URLs that follow the
 * request, which choose the key with {@link MediaResolver}, and by the URL parts, which take it from their params.
 */
final class MediaLookup
{
    private MediaLookup()
    {
    }

    /**
     * @param key content id, or content path when it starts with {@code /}
     * @return the content
     * @throws IllegalArgumentException  if there is no key
     * @throws ContentNotFoundException if there is no such content
     */
    static Content content( final ContentService contentService, final ProjectName projectName, final Branch branch, final String key )
    {
        if ( key == null )
        {
            throw new IllegalArgumentException( "Either id or path is required" );
        }

        final Content content = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( projectName.getRepoId() )
            .branch( branch )
            .build()
            .callWith( () -> key.startsWith( "/" )
                ? contentService.getByPath( ContentPath.from( key ) )
                : contentService.getById( ContentId.from( key ) ) );

        if ( content == null )
        {
            throw notFound( projectName, branch, key );
        }
        return content;
    }

    /**
     * @return the media content
     * @throws ContentNotFoundException if there is no such content, or it is not a media
     */
    static Media media( final ContentService contentService, final ProjectName projectName, final Branch branch, final String key )
    {
        if ( content( contentService, projectName, branch, key ) instanceof Media media )
        {
            return media;
        }
        throw notFound( projectName, branch, key );
    }

    /**
     * @return the media, when it is an image or a vector image
     * @throws IllegalArgumentException otherwise
     */
    static Media image( final Media media )
    {
        if ( media.getType().isImageMedia() || media.getType().isVectorMedia() )
        {
            return media;
        }
        throw new IllegalArgumentException( String.format( "Content [%s] is not an image", media.getId() ) );
    }

    private static ContentNotFoundException notFound( final ProjectName projectName, final Branch branch, final String key )
    {
        final ContentNotFoundException.Builder ex = ContentNotFoundException.create().repositoryId( projectName.getRepoId() ).branch( branch );

        if ( key.startsWith( "/" ) )
        {
            ex.contentPath( ContentPath.from( key ) );
        }
        else
        {
            ex.contentId( ContentId.from( key ) );
        }

        return ex.build();
    }
}
