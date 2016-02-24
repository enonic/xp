package com.enonic.xp.portal.impl.handler.mapping;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;

final class MappingHandlerWorker
    extends PortalHandlerWorker
{
    protected ResourceService resourceService;

    protected ControllerScriptFactory controllerScriptFactory;

    protected ControllerMappingDescriptor mappingDescriptor;

    @Override
    public void execute()
        throws Exception
    {
        final ControllerScript controllerScript = getScript();

        this.request.setApplicationKey( mappingDescriptor.getApplication() );

        this.response = PortalResponse.create( controllerScript.execute( this.request ) );
    }

    private ControllerScript getScript()
    {
        final Resource resource = this.resourceService.getResource( mappingDescriptor.getController() );
        if ( !resource.exists() )
        {
            throw notFound( "Controller [%s] not found", mappingDescriptor.getController().toString() );
        }
        return this.controllerScriptFactory.fromScript( resource.getKey() );
    }

}
