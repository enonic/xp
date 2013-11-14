package com.enonic.wem.api.command.content.site;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKeys;

public final class GetSiteTemplateByKeys
    extends Command<SiteTemplate>
{
    private SiteTemplateKeys keys;

    public GetSiteTemplateByKeys( final SiteTemplateKeys keys )
    {
        this.keys = keys;
    }

    public SiteTemplateKeys getKeys()
    {
        return keys;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.keys );
    }
}
