package com.enonic.wem.core.content.page;

import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.CreatePageTemplateParams;
import com.enonic.wem.api.content.page.DeletePageTemplateParams;
import com.enonic.wem.api.content.page.GetPageTemplateByKey;
import com.enonic.wem.api.content.page.GetPageTemplatesBySiteTemplate;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.PageTemplates;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public class PageTemplateServiceImpl
    implements PageTemplateService
{
    @Inject
    private Client client;

    public PageTemplate create( final CreatePageTemplateParams params )
    {
        return client.execute( params );
    }

    public boolean delete( final DeletePageTemplateParams params )
    {
        return client.execute( params );
    }

    public PageTemplate getByKey( final PageTemplateKey pageTemplateKey, final SiteTemplateKey siteTemplateKey )
    {
        final GetPageTemplateByKey command = new GetPageTemplateByKey().key( pageTemplateKey ).siteTemplateKey( siteTemplateKey );
        return client.execute( command );
    }

    public PageTemplates getBySiteTemplate( final SiteTemplateKey siteTemplateKey )
    {
        final GetPageTemplatesBySiteTemplate command = new GetPageTemplatesBySiteTemplate().siteTemplate( siteTemplateKey );
        return client.execute( command );
    }
}
