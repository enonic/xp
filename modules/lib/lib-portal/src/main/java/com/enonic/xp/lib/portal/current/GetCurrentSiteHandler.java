package com.enonic.xp.lib.portal.current;

import java.util.Objects;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.lib.content.mapper.SiteMapper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.site.Site;

public final class GetCurrentSiteHandler
    implements ScriptBean
{
    private PortalRequest request;

    public SiteMapper execute()
    {
        final Site site = getSite();
        return site != null ? new SiteMapper( site ) : null;
    }

    private Site getSite()
    {
        final Site site = this.request.getSite();
        if ( site != null &&
            site.getPermissions().isAllowedFor( ContextAccessor.current().getAuthInfo().getPrincipals(), Permission.READ ) )
        {
            return site;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.request = Objects.requireNonNull( context.getBinding( PortalRequest.class ).get(), "no request bound" );
    }
}
