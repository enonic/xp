package com.enonic.xp.admin.impl.app;

import java.io.IOException;
import java.net.URL;

public interface ResourceLocator
{
    boolean shouldCache();

    URL findResource( String name )
        throws IOException;
}
