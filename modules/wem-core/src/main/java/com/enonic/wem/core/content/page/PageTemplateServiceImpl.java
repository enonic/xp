package com.enonic.wem.core.content.page;

import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.content.page.CreatePageTemplateParams;
import com.enonic.wem.api.content.page.DeletePageTemplateParams;
import com.enonic.wem.api.content.page.GetPageTemplateByKey;
import com.enonic.wem.api.content.page.GetPageTemplatesBySiteTemplate;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.PageTemplates;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.schema.content.ContentTypeName;

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

    @Override
    public PageTemplate getDefault( final SiteTemplateKey siteTemplateKey, final ContentTypeName contentType )
    {
        final SiteTemplate siteTemplate = client.execute( new GetSiteTemplateByKey( siteTemplateKey ) );
        return siteTemplate.getDefaultPageTemplate( contentType );
    }

    public PageTemplates getBySiteTemplate( final SiteTemplateKey siteTemplateKey )
    {
        final GetPageTemplatesBySiteTemplate command = new GetPageTemplatesBySiteTemplate().siteTemplate( siteTemplateKey );
        return client.execute( command );
    }
}
