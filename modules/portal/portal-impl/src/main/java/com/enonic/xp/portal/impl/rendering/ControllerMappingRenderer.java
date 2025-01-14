package com.enonic.xp.portal.impl.rendering;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.processor.ProcessorChainResolver;
import com.enonic.xp.portal.impl.processor.ResponseProcessorExecutor;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;

@Component(immediate = true, service = Renderer.class)
public final class ControllerMappingRenderer
    extends PostProcessingRenderer<ControllerMappingDescriptor>
{
    @Activate
    public ControllerMappingRenderer( @Reference final PostProcessor postProcessor, @Reference final PortalScriptService scriptService,
                                      @Reference final ProcessorChainResolver processorChainResolver )
    {
        super( postProcessor, new ResponseProcessorExecutor( scriptService ), processorChainResolver );
    }

    @Override
    public Class<ControllerMappingDescriptor> getType()
    {
        return ControllerMappingDescriptor.class;
    }

    @Override
    public PortalResponse doRender( final ControllerMappingDescriptor descriptor, final PortalRequest portalRequest )
    {
        final PortalResponse portalResponse = portalRequest.getControllerScript().execute( portalRequest );
        return isServiceMapping( descriptor ) ? PortalResponse.create( portalResponse ).applyFilters( false ).build() : portalResponse;
    }

    @Override
    protected boolean isPageContributionsAllowed( final ControllerMappingDescriptor controllerMappingDescriptor, final PortalRequest portalRequest )
    {
        return !isServiceMapping( controllerMappingDescriptor );
    }

    private boolean isServiceMapping( final ControllerMappingDescriptor controllerMappingDescriptor )
    {
        return controllerMappingDescriptor.getService() != null;
    }
}
