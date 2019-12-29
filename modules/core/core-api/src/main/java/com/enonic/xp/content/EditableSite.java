package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigs;

@PublicApi
public class EditableSite
    extends EditableContent
{
    public SiteConfigs siteConfigs;

    public EditableSite( final Site source )
    {
        super( source );
        this.siteConfigs = source.getSiteConfigs();
    }

    @Override
    public Site build()
    {
        return (Site) super.build();
    }
}
