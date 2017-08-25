package com.enonic.xp.testkit.portal;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.view.ViewFunctionService;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.testkit.ScriptTestSupport;
import com.enonic.xp.testkit.mock.MockServiceRegistry;
import com.enonic.xp.testkit.mock.MockViewFunctionService;

public abstract class PortalTestSupport
    extends ScriptTestSupport
{
    private PortalRequest request;

    @Override
    public void initialize()
    {
        super.initialize();
        PortalRequestAccessor.set( this.request );
    }

    @Override
    public void setupSettings( final ScriptSettings.Builder builder )
    {
        super.setupSettings( builder );

        this.request = new PortalRequest();
        setupRequest( this.request );

        builder.binding( PortalRequest.class, () -> this.request );
    }

    @Override
    public void setupServices( final MockServiceRegistry registry )
    {
        super.setupServices( registry );

        registry.register( ViewFunctionService.class, new MockViewFunctionService() );
    }

    public void setupRequest( final PortalRequest request )
    {
        request.setMode( RenderMode.LIVE );
        request.setBranch( Branch.from( "draft" ) );
        request.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        request.setBaseUri( "/portal" );

        final Content content = Content.create().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        request.setContent( content );
    }
}
