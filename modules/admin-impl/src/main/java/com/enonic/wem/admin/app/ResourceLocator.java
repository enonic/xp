package com.enonic.wem.admin.app;

import java.io.IOException;
import java.net.URL;

public interface ResourceLocator
{
    public URL findResource( String name )
        throws IOException;
}
