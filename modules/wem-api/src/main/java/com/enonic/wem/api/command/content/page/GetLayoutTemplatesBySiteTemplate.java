package com.enonic.wem.api.command.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.layout.LayoutTemplates;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public class GetLayoutTemplatesBySiteTemplate
    extends Command<LayoutTemplates>
{
    private SiteTemplateKey siteTemplate;

    public GetLayoutTemplatesBySiteTemplate siteTemplate( final SiteTemplateKey siteTemplate )
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
