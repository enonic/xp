package com.enonic.xp.impl.server.rest;

import java.util.List;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.impl.server.rest.model.WebappJson;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

/**
 * Lists the installed applications that ship a webapp, shared by the legacy {@code /webapps/list} resource and the
 * {@code server:webapp} API.
 */
public final class WebappJsonFactory
{
    private final ApplicationService applicationService;

    private final ResourceService resourceService;

    public WebappJsonFactory( final ApplicationService applicationService, final ResourceService resourceService )
    {
        this.applicationService = applicationService;
        this.resourceService = resourceService;
    }

    public List<WebappJson> list()
    {
        return applicationService.getInstalledApplications()
            .stream()
            .map( Application::getKey )
            .map( key -> ResourceKey.from( key, "/webapp/webapp.js" ) )
            .map( resourceService::getResource )
            .filter( Resource::exists )
            .map( Resource::getKey )
            .map( ResourceKey::getApplicationKey )
            .map( WebappJson::from )
            .toList();
    }
}
