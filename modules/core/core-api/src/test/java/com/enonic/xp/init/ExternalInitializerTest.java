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
        return TestExternalInitializer.create().
            setInitializationCheckMaxCount( 2l ).
            setInitializationCheckPeriod( 1l ).
            setIndexService( indexService ).
            build();
    }
}
