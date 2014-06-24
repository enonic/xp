package com.enonic.wem.portal.underscore;

import javax.inject.Inject;

import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.portal.base.ModuleBaseResource;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsControllerFactory;
import com.enonic.wem.portal.controller.JsHttpRequest;
import com.enonic.wem.portal.controller.JsHttpResponseSerializer;
import com.enonic.wem.portal.rendering.RenderResult;
import com.enonic.wem.portal.script.lib.PortalUrlScriptBean;

public final class ServiceResource
    extends ModuleBaseResource
{
    @Inject
    protected JsControllerFactory controllerFactory;

    @Override
    protected Representation doHandle()
        throws ResourceException
    {
        final ModuleKey moduleKey = resolveModule();
        final JsContext context = new JsContext();

        final JsHttpRequest jsRequest = new JsHttpRequest();
        jsRequest.setMode( this.mode );
        jsRequest.setMethod( getRequest().getMethod().toString() );
        jsRequest.addParams( getParams() );
        context.setRequest( jsRequest );

        final PortalUrlScriptBean portalUrlScriptBean = new PortalUrlScriptBean();
        context.setPortalUrlScriptBean( portalUrlScriptBean );

        final JsController controller = this.controllerFactory.newController();

        final String serviceName = getAttribute( "service" );
        controller.scriptDir( ModuleResourceKey.from( moduleKey, "service/" + serviceName ) );
        controller.context( context );
        controller.execute();

        final RenderResult result = new JsHttpResponseSerializer( context.getResponse() ).serialize();
        return toRepresentation( result );
    }
}
