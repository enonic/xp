package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.repository.RepositoryUtils;

final class PageUrlBuilder
    extends PortalUrlBuilder<PageUrlParams>
{
    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );

        if ( this.portalRequest.getRawPath().startsWith( "/api/" ) )
        {
            url.setLength( 0 );
            appendPart( url, RepositoryUtils.getContentRepoName( this.portalRequest.getRepositoryId() ) );
            appendPart( url, this.portalRequest.getBranch().toString() );
            setMustBeRewritten( false );
        }

        final ContentPath resolved = resolvePath();
        appendPart( url, resolved.toString() );
    }

    private ContentPath resolvePath()
    {
        return new ContentPathResolver().
            portalRequest( this.portalRequest ).
            contentService( this.contentService ).
            id( this.params.getId() ).
            path( this.params.getPath() ).
            resolve();
    }
}
