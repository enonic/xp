package com.enonic.xp.init;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.index.IndexService;

public class ExternalInitializerTest
{
    @Test
    public void testIsMaster()
    {
        final IndexService indexService = Mockito.mock( IndexService.class );
        final ExternalInitializer initializer = createExternalInitializer( indexService );

        Mockito.when( indexService.isMaster() ).thenReturn( true );
        Assert.assertEquals( true, initializer.isMaster() );

        Mockito.when( indexService.isMaster() ).thenReturn( false );
        Assert.assertEquals( false, initializer.isMaster() );
    }

    private ExternalInitializer createExternalInitializer( final IndexService indexService )
    {
        return new ExternalInitializer( indexService )
        {
            @Override
            protected boolean isInitialized()
            {
                return false;
            }

            @Override
            protected void doInitialize()
            {

            }

            @Override
            protected String getInitializationSubject()
            {
                return null;
            }
        };
    }
}
