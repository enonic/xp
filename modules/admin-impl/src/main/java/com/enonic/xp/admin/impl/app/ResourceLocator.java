package com.enonic.xp.admin.impl.app;

import java.io.IOException;
import java.net.URL;

public interface ResourceLocator
{
    public boolean shouldCache();

    public URL findResource( String name )
        throws IOException;
}
