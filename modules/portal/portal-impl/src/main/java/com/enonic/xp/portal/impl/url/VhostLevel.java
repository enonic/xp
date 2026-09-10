package com.enonic.xp.portal.impl.url;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

/**
 * A virtual host mapping states which part of the content tree an address stands for: a target of
 * {@code /site/myproject/master/features} says that its source is the address of {@code /features}.
 * That is the same statement a caller makes by selecting a site, so it decides which level URLs
 * following the current request belong to.
 */
final class VhostLevel
{
    private VhostLevel()
    {
    }

    /**
     * @return the level the current site request mounts: the content path of the matched virtual
     * host target, and the root of the project when no virtual host narrows the request - the
     * site engine then serves the project as a whole. {@code null} when there is no site request,
     * so that the content decides its level itself
     */
    static ContentPath resolve( final ProjectName projectName, final Branch branch )
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        if ( !PortalRequestHelper.isSiteBase( portalRequest ) )
        {
            return null;
        }

        if ( portalRequest.getRawRequest() == null )
        {
            return ContentPath.ROOT;
        }

        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( portalRequest.getRawRequest() );
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
