package com.enonic.wem.core.content.page;

import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.page.CreatePageTemplate;
import com.enonic.wem.api.command.content.page.DeletePageTemplate;
import com.enonic.wem.api.command.content.page.GetPageTemplateByKey;
import com.enonic.wem.api.command.content.page.GetPageTemplatesBySiteTemplate;
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

    public PageTemplate create( final CreatePageTemplate command )
    {
        return client.execute( command );
    }

    public boolean delete( final DeletePageTemplate command )
    {
        return client.execute( command );
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
