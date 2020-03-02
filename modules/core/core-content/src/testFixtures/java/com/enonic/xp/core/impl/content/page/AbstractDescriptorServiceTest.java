package com.enonic.xp.core.impl.content.page;

import org.mockito.Mockito;

import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.form.Form;
import com.enonic.xp.schema.mixin.MixinService;

public abstract class AbstractDescriptorServiceTest
    extends ApplicationTestSupport
{
    protected MixinService mixinService;

    @Override
    protected void initialize()
        throws Exception
    {
        this.mixinService = Mockito.mock( MixinService.class );
        Mockito.when( this.mixinService.inlineFormItems( Mockito.any() ) ).thenReturn( Form.create().build() );

        addApplication( "myapp1", "/apps/myapp1" );
        addApplication( "myapp2", "/apps/myapp2" );
    }
}
