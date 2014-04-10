package com.enonic.wem.core.content.site;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.api.content.site.SiteService;
import com.enonic.wem.api.content.site.SiteTemplateService;

public class SiteModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( SiteTemplateService.class ).to( SiteTemplateServiceImpl.class ).in( Singleton.class );
        bind( SiteService.class ).to( SiteServiceImpl.class ).in( Singleton.class );
    }
}
