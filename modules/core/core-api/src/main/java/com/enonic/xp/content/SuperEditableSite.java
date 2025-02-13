package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigs;

@PublicApi
public class SuperEditableSite
    extends SuperEditableContent
{
    public EditableFieldPolicyWrapper<SiteConfigs> siteConfigs;

    public SuperEditableSite( final Site source )
    {
        super( source );
        this.siteConfigs = new EditableFieldPolicyWrapper<>( source.getSiteConfigs() );
    }

    @Override
    public Site build()
    {
        return (Site) super.build();
    }
}
