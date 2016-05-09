package com.enonic.xp.admin.impl.portal;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.PortalWebHandlerWorker;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebResponse;

final class WidgetWebHandlerWorker
    extends PortalWebHandlerWorker
{
    private final ResourceKey scriptDir;

    private final ControllerScriptFactory controllerScriptFactory;

    private WidgetWebHandlerWorker( final Builder builder )
    {
        super( builder );
        scriptDir = builder.scriptDir;
        controllerScriptFactory = builder.controllerScriptFactory;
    }

    public static Builder create()
    {
        return new Builder();
    }


    @Override
    public WebResponse execute()
    {

        final PortalWebRequest portalWebRequest = PortalWebRequest.create( webRequest ).
            applicationKey( this.scriptDir.getApplicationKey() ).
            baseUri( WidgetWebHandler.ADMIN_WIDGET_PREFIX + scriptDir.getApplicationKey() + "/" + scriptDir.getName() ).
            build();
        final PortalRequest portalRequest = convertToPortalRequest( portalWebRequest );

        final ControllerScript controllerScript = this.controllerScriptFactory.fromDir( this.scriptDir );
        final PortalResponse portalResponse = PortalResponse.create( controllerScript.execute( portalRequest ) ).build();
        return convertToPortalWebResponse( portalResponse );
    }


    public static final class Builder
        extends PortalWebHandlerWorker.Builder<Builder, WebRequest, WebResponse>
    {
        private ResourceKey scriptDir;

        private ControllerScriptFactory controllerScriptFactory;

        private Builder()
        {
        }

        public Builder scriptDir( final ResourceKey scriptDir )
        {
            this.scriptDir = scriptDir;
            return this;
        }

        public Builder controllerScriptFactory( final ControllerScriptFactory controllerScriptFactory )
        {
            this.controllerScriptFactory = controllerScriptFactory;
            return this;
        }

        public WidgetWebHandlerWorker build()
        {
            return new WidgetWebHandlerWorker( this );
        }
    }
}
