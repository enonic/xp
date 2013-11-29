package com.enonic.wem.api.command.content.page.part;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.part.PartTemplates;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public class GetPartTemplatesBySiteTemplate
    extends Command<PartTemplates>
{
    private SiteTemplateKey siteTemplate;

    public GetPartTemplatesBySiteTemplate siteTemplate( final SiteTemplateKey siteTemplate )
    {
        this.siteTemplate = siteTemplate;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( siteTemplate, "siteTemplate is required" );
    }

    public SiteTemplateKey getSiteTemplate()
    {
        return siteTemplate;
    }
}
