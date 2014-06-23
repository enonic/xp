package com.enonic.wem.launcher.resolver;

import java.io.File;

public interface ArtifactResolver
{
    public File resolve( String uri );
}
