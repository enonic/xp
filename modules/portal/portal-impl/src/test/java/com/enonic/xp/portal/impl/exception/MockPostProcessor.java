package com.enonic.xp.portal.impl.exception;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.postprocess.PostProcessor;

final class MockPostProcessor
    implements PostProcessor
{
    private boolean executed;

    @Override
    public PortalResponse processResponse( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        executed = true;
        if ( portalResponse.isPostProcess() && portalResponse.getBody() instanceof String )
        {
            return applyInstruction( portalResponse );
        }
        return portalResponse;
    }

    @Override
    public PortalResponse processResponseInstructions( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        executed = true;
        if ( portalResponse.isPostProcess() && portalResponse.getBody() instanceof String )
        {
            return applyInstruction( portalResponse );
        }
        return portalResponse;
    }

    @Override
    public PortalResponse processResponseContributions( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        executed = true;
        return portalResponse;
    }

    private PortalResponse applyInstruction( final PortalResponse portalResponse )
    {
        final String body = ( (String) portalResponse.getBody() ).replace( "<!--#COMPONENT module:myPart -->", "<h3>My Part</h3>" );
        return PortalResponse.create( portalResponse ).body( body ).build();
    }

    public boolean isExecuted()
    {
        return executed;
    }
}
