package com.enonic.xp.lib.portal.current;

import com.enonic.xp.lib.content.mapper.SiteMapper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.bean.BeanContext;
import com.enonic.xp.portal.bean.ScriptBean;
import com.enonic.xp.site.Site;

public final class GetCurrentSiteHandler
    implements ScriptBean
{
    private PortalRequest request;

    public SiteMapper execute()
    {
        final Site site = this.request.getSite();
        return site != null ? convert( site ) : null;
    }

    private SiteMapper convert( final Site site )
    {
        return site == null ? null : new SiteMapper( site );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.request = context.getRequest().get();
    }
}
