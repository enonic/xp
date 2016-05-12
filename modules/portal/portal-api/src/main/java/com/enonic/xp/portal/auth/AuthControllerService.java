package com.enonic.xp.portal.auth;

import java.io.IOException;

public interface AuthControllerService
{
    boolean execute( AuthControllerExecutionParams params )
        throws IOException;
}
