package com.enonic.wem.core.schema.mixin;


import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.schema.mixin.GetMixins;
import com.enonic.wem.api.schema.mixin.Mixins;

public class MixinsInitializerTest
{
    @Test
    public void demo()
        throws Exception
    {
        Client client = Mockito.mock( Client.class );
        Mockito.when( client.execute( Mockito.any( GetMixins.class ) ) ).thenReturn( Mixins.empty() );

        MixinsInitializer mixinsInitializer = new MixinsInitializer();
        mixinsInitializer.setClient( client );
        mixinsInitializer.initialize();
    }
}
