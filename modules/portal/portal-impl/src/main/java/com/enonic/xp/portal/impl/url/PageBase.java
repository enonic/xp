package com.enonic.xp.portal.impl.url;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.url.BaseUrlParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PageUrlPartsParams;

/**
 * The two things a page URL is made of: the level of the content tree it belongs to - the
 * nearest site at or above it, or the project - which decides the base URL and what the path is
 * relative to, and the content it addresses.
 */
final class PageBase
{
    private PageBase()
    {
    }

    /**
     * @return the parameters the base URL of a page URL is resolved from: the content's own
     */
    static BaseUrlParams params( final PageUrlParams params )
    {
        return BaseUrlParams.create()
            .setUrlType( params.getType() )
            .setProjectName( params.getProjectName() )
            .setBranch( params.getBranch() )
            .setId( params.getId() )
            .setPath( params.getPath() )
            .build();
    }

    /**
     * @return the level a page URL belongs to: the one the matched virtual host mounts, and the
     * content's own otherwise
     */
    static ContentPath level( final BaseUrlMetadata baseUrlMetadata )
    {
        final ContentPath mounted = VhostLevel.resolve( baseUrlMetadata.projectName(), baseUrlMetadata.branch() );

        return mounted != null ? mounted : baseUrlMetadata.anchorPath();
    }

    /**
     * @return the path of the content page URL parts address, resolved separately from their base
     */
    static ContentPath contentPath( final ContentService contentService, final PageUrlPartsParams params )
    {
        if ( params.getId() != null )
        {
            return contentService.getById( ContentId.from( params.getId() ) ).getPath();
        }

        final ContentPath path = ContentPath.from( params.getPath() );
        return path.isRoot() ? ContentPath.ROOT : contentService.getByPath( path ).getPath();
    }
}
