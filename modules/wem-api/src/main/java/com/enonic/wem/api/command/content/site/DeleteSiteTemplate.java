package com.enonic.wem.api.command.content.site;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public final class DeleteSiteTemplate
    extends Command<SiteTemplateKey>
{
    private SiteTemplateKey key;

    public DeleteSiteTemplate( SiteTemplateKey key )
    {
        this.key = key;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( key );
    }

    public SiteTemplateKey getKey()
    {
        return key;
    }
}
