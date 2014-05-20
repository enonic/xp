package com.enonic.wem.core.initializer;

public interface StartupInitializer
{
    public void initialize( boolean reinit )
        throws Exception;
}
