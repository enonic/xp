package com.enonic.wem.admin.rest.resource.content.site.template.json;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.rest.resource.content.site.template.SiteTemplateIconUrlResolver;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplates;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplates;

public final class ListTemplateItemJson
{
    private final List<TemplateItemJson> list;

    public ListTemplateItemJson( final SiteTemplates siteTemplates, final SiteTemplateIconUrlResolver iconUrlResolver )
    {
        ImmutableList.Builder<TemplateItemJson> builder = ImmutableList.builder();
        for ( SiteTemplate siteTemplate : siteTemplates )
        {
            builder.add( new TemplateItemJson( siteTemplate, iconUrlResolver ) );
        }
        this.list = builder.build();
    }

    public ListTemplateItemJson( final SiteTemplateKey siteTemplateKey, final PageTemplates pageTemplates )
    {
        ImmutableList.Builder<TemplateItemJson> builder = ImmutableList.builder();
        for ( PageTemplate pageTemplate : pageTemplates )
        {
            builder.add( new TemplateItemJson( siteTemplateKey, pageTemplate ) );
        }
        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<TemplateItemJson> getTemplates()
    {
        return this.list;
    }
}
