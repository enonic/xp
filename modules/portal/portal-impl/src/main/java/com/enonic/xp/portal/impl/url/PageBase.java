package com.enonic.xp.portal.impl.url;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.url.BaseUrlParams;
import com.enonic.xp.portal.url.PageUrlParams;

/**
 * The two things a page URL is made of: the level of the content tree it belongs to - the
 * nearest site at or above it, or the project - which decides the base URL and what the path is
 * relative to, and the content it addresses. They are the same content unless the caller selects
 * a level of its own.
 */
final class PageBase
{
    private PageBase()
    {
    }

    /**
     * @return the parameters the base URL is resolved from: the ones the caller selected a level
     * with, or the content's own when it selected none
     */
    static BaseUrlParams params( final PageUrlParams params )
    {
        if ( params.getBase() != null )
        {
            return params.getBase();
        }

        return BaseUrlParams.create()
            .setUrlType( params.getType() )
            .setProjectName( params.getProjectName() )
            .setBranch( params.getBranch() )
            .setId( params.getId() )
            .setPath( params.getPath() )
            .build();
    }

    /**
     * @return the level the URL belongs to: the one the caller selected, the one the matched
     * virtual host mounts when it selected none, and the content's own otherwise
     */
    static ContentPath level( final PageUrlParams params, final BaseUrlMetadata baseUrlMetadata )
    {
        if ( params.getBase() != null )
        {
            return baseUrlMetadata.getAnchorPath();
        }

        final ContentPath mounted = VhostLevel.resolve( baseUrlMetadata.getProjectName(), baseUrlMetadata.getBranch() );

        return mounted != null ? mounted : baseUrlMetadata.getAnchorPath();
    }

    /**
     * @return the path of the content the URL addresses, which is resolved separately from the
     * base only when the caller selected a level of its own
     */
    static ContentPath contentPath( final ContentService contentService, final PageUrlParams params,
                                    final BaseUrlMetadata baseUrlMetadata )
    {
        if ( params.getBase() == null )
        {
            return baseUrlMetadata.getContent().getPath();
        }

        if ( params.getId() != null )
        {
            return contentService.getById( ContentId.from( params.getId() ) ).getPath();
        }

        final ContentPath path = ContentPath.from( params.getPath() );
        return path.isRoot() ? ContentPath.ROOT : contentService.getByPath( path ).getPath();
    }
}
