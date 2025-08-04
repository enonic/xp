package com.enonic.xp.core.impl.content.page;

import org.mockito.Mockito;

import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.core.impl.app.descriptor.DescriptorFacetFactoryImpl;
import com.enonic.xp.core.impl.app.descriptor.DescriptorServiceImpl;
import com.enonic.xp.core.impl.content.page.region.LayoutDescriptorLoader;
import com.enonic.xp.core.impl.content.page.region.PartDescriptorLoader;
import com.enonic.xp.descriptor.DescriptorService;
import com.enonic.xp.form.Form;
import com.enonic.xp.schema.mixin.MixinService;

public abstract class AbstractDescriptorServiceTest
    extends ApplicationTestSupport
{
    protected MixinService mixinService;

    protected DescriptorService descriptorService;

    @Override
    protected void initialize()
        throws Exception
    {
        this.mixinService = Mockito.mock( MixinService.class );
        Mockito.when( this.mixinService.inlineFormItems( Mockito.any() ) ).thenReturn( Form.empty() );

        final DescriptorFacetFactoryImpl facetFactory = new DescriptorFacetFactoryImpl( this.applicationService, this.resourceService );

        final DescriptorServiceImpl descriptorServiceImpl = new DescriptorServiceImpl( facetFactory );
        descriptorServiceImpl.addLoader( new LayoutDescriptorLoader( this.resourceService, this.mixinService ) );
        descriptorServiceImpl.addLoader( new PartDescriptorLoader( this.resourceService, this.mixinService ) );
        descriptorServiceImpl.addLoader( new PageDescriptorLoader( this.resourceService, this.mixinService ) );
        this.descriptorService = descriptorServiceImpl;

        addApplication( "myapp1", "/apps/myapp1" );
        addApplication( "myapp2", "/apps/myapp2" );
    }
}
