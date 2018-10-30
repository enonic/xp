package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigs;

@Beta
public class EditableSite
    extends EditableContent
{
    public SiteConfigs siteConfigs;

    public EditableSite( final Site source )
    {
        super( source );
        this.siteConfigs = source.getSiteConfigs();
    }

    public Site build()
    {
        return (Site) super.build();
    }
}
