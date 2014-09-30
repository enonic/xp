package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.PageTemplates;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.schema.content.ContentTypeName;

public final class PageTemplateServiceImpl
    implements PageTemplateService
{
    private SiteTemplateService siteTemplateService;

    public PageTemplate getByKey( final PageTemplateKey pageTemplateKey, final SiteTemplateKey siteTemplateKey )
    {
        return new GetPageTemplateByKeyCommand().pageTemplateKey( pageTemplateKey ).siteTemplateKey( siteTemplateKey ).siteTemplateService(
            this.siteTemplateService ).execute();
    }

    @Override
    public PageTemplate getDefault( final SiteTemplateKey siteTemplateKey, final ContentTypeName contentType )
    {
        final SiteTemplate siteTemplate = this.siteTemplateService.getSiteTemplate( siteTemplateKey );
        return siteTemplate.getDefaultPageTemplate( contentType );
    }

    public PageTemplates getBySiteTemplate( final SiteTemplateKey siteTemplateKey )
    {
        return this.siteTemplateService.getSiteTemplate( siteTemplateKey ).getPageTemplates();
    }

    public void setSiteTemplateService( final SiteTemplateService siteTemplateService )
    {
        this.siteTemplateService = siteTemplateService;
    }
}
