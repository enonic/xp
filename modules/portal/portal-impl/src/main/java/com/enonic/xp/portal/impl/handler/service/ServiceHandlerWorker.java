package com.enonic.xp.portal.impl.handler.service;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.ControllerHandlerWorker;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.Site;

final class ServiceHandlerWorker
    extends ControllerHandlerWorker
{
    private final static String ROOT_ASSET_PREFIX = "services/";

    private final static String SITE_ASSET_PREFIX = "site/services/";

    protected ResourceService resourceService;

    protected ApplicationKey applicationKey;

    protected String name;

    protected ControllerScriptFactory controllerScriptFactory;

    @Override
    public void execute()
        throws Exception
    {
        this.request.setApplicationKey( applicationKey );
        final Content content = getContentOrNull( getContentSelector() );
        this.request.setContent( content );
        final Site site = getSiteOrNull( content );
        this.request.setSite( site );

        Resource resource = this.resourceService.getResource( ResourceKey.from( this.applicationKey, ROOT_ASSET_PREFIX + this.name ) );
        if ( !resource.exists() )
        {
            resource = this.resourceService.getResource( ResourceKey.from( this.applicationKey, SITE_ASSET_PREFIX + this.name ) );
            if ( !resource.exists() )
            {
                throw notFound( "Resource [%s] not found", ResourceKey.from( this.applicationKey, ROOT_ASSET_PREFIX + this.name ) );
            }
        }

        final ControllerScript controllerScript = this.controllerScriptFactory.fromDir( resource.getKey() );
        this.response = PortalResponse.create( controllerScript.execute( this.request ) );
    }
}
