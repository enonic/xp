package com.enonic.wem.portal.dispatch;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.ImplementedBy;

@ImplementedBy(PortalDispatcherImpl.class)
public interface PortalDispatcher
{
    public void dispatch( HttpServletRequest req, HttpServletResponse res )
        throws IOException;
}
