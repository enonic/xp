package com.enonic.xp.init;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.index.IndexService;

import static org.junit.jupiter.api.Assertions.*;

public class ExternalInitializerTest
{
    @Test
    public void testIsMaster()
    {
        final IndexService indexService = Mockito.mock( IndexService.class );
        final ExternalInitializer initializer = createExternalInitializer( indexService );

        Mockito.when( indexService.isMaster() ).thenReturn( true );
        assertEquals( true, initializer.isMaster() );

        Mockito.when( indexService.isMaster() ).thenReturn( false );
        assertEquals( false, initializer.isMaster() );
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
