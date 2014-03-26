package com.enonic.wem.core.schema.mixin;


import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.schema.mixin.GetMixinParams;
import com.enonic.wem.api.command.schema.mixin.MixinService;

public class MixinsInitializerTest
{
    @Test
    public void demo()
        throws Exception
    {
        final MixinService mixinService = Mockito.mock( MixinService.class );
        Mockito.when( mixinService.getByName( Mockito.any( GetMixinParams.class ) ) ).thenReturn( null );

        MixinsInitializer mixinsInitializer = new MixinsInitializer();
        mixinsInitializer.setMixinService( mixinService );
        mixinsInitializer.initialize();
    }
}
