package com.enonic.xp.portal.impl.url;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.web.vhost.VirtualHost;

/**
 * A virtual host mapping says which part of the content tree an address stands for: a target of
 * {@code /site/myproject/master/features} means its source is the address of {@code /features}.
 * That decides which level URLs built from the current request belong to.
 */
final class VhostLevel
{
    private VhostLevel()
    {
    }

    /**
     * @return the level the current site request is mounted at: the content path of the matched
     * virtual host target, or the project root when no virtual host narrows the request; {@code null}
     * when there is no site request, so the content decides its level itself
     */
    static ContentPath resolve( final ProjectName projectName, final Branch branch )
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        if ( !PortalRequestHelper.isSiteBase( portalRequest ) )
        {
            return null;
        }

        final VirtualHost virtualHost = portalRequest.getVirtualHost();
        if ( virtualHost == null )
        {
            return ContentPath.ROOT;
        }

        final String mount = portalRequest.getBaseUri() + "/" + projectName + "/" + branch;
        final String target = virtualHost.getTarget();

        if ( !target.startsWith( mount ) )
        {
            return ContentPath.ROOT;
        }

        final String contentPath = target.substring( mount.length() );

        return contentPath.isEmpty() || "/".equals( contentPath ) ? ContentPath.ROOT : ContentPath.from( contentPath );
    }
}
