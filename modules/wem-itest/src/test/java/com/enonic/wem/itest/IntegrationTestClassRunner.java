package com.enonic.wem.itest;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public final class IntegrationTestClassRunner
    extends BlockJUnit4ClassRunner
{
    public IntegrationTestClassRunner( final Class<?> clazz )
        throws InitializationError
    {
        super( clazz );
    }
}
