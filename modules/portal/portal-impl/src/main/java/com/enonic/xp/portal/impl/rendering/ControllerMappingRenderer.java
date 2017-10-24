package com.enonic.xp.portal.impl.rendering;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.filter.FilterChainResolver;
import com.enonic.xp.portal.impl.filter.FilterExecutor;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;

@Component(immediate = true, service = Renderer.class)
public final class ControllerMappingRenderer
    extends PostProcessingRenderer<ControllerMappingDescriptor>
{
    @Override
    public Class<ControllerMappingDescriptor> getType()
    {
        return ControllerMappingDescriptor.class;
    }

    @Override
    public PortalResponse doRender( final ControllerMappingDescriptor controllerMappingDescriptor, final PortalRequest portalRequest )
    {
        return portalRequest.getControllerScript().execute( portalRequest );
    }

    @Reference
    public void setPostProcessor( final PostProcessor postProcessor )
    {
        this.postProcessor = postProcessor;
    }

    @Reference
    public void setScriptService( final PortalScriptService scriptService )
    {
        this.filterExecutor = new FilterExecutor( scriptService );
    }


    @Reference
    public void setFilterChainResolver( final FilterChainResolver filterChainResolver )
    {
        this.filterChainResolver = filterChainResolver;
    }
}
