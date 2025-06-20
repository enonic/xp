package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;

@PublicApi
public class EditableSite
    extends EditableContent
{
    public SiteConfigs siteConfigs;

    public EditableSite( final Site source )
    {
        super( source );
        this.siteConfigs = new SiteConfigsDataSerializer().fromProperties( source.getData().getRoot() ).build();
    }

    @Override
    public Site build()
    {
        return (Site) super.build();
    }
}
