package com.enonic.wem.core.initializer;

public interface StartupInitializer
{
    public void cleanData()
        throws Exception;

    public void initializeData()
        throws Exception;
}
