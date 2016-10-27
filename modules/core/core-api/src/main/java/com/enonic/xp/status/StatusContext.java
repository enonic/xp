package com.enonic.xp.status;

import java.io.OutputStream;
import java.util.Optional;

public interface StatusContext
{
    Optional<String> getParameter( String name );

    OutputStream getOutputStream();
}
