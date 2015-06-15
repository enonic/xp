package com.enonic.xp.core.impl.bean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.blueprint.container.NoSuchComponentException;

import com.enonic.xp.module.ModuleKey;

public class BeanManagerImplTest
{
    private BeanManagerImpl manager;

    private BundleContext context;

    private ServiceReference serviceRef;

    private BlueprintContainer container;

    @Before
    public void setup()
    {
        this.manager = new BeanManagerImpl();
        this.context = Mockito.mock( BundleContext.class );
        this.manager.initialize( this.context );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( "foo.bar" );

        this.serviceRef = Mockito.mock( ServiceReference.class );
        Mockito.when( this.serviceRef.getBundle() ).thenReturn( bundle );

        this.container = Mockito.mock( BlueprintContainer.class );
        Mockito.when( this.context.getService( this.serviceRef ) ).thenReturn( this.container );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBean_containerNotFound()
    {
        this.manager.getBean( ModuleKey.from( "foo.bar" ), "mybean" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBean_beanNotFound()
    {
        Mockito.when( this.container.getComponentInstance( "mybean" ) ).thenThrow( new NoSuchComponentException( "mybean" ) );

        this.manager.addContainer( this.serviceRef );
        this.manager.getBean( ModuleKey.from( "foo.bar" ), "mybean" );
    }

    @Test
    public void getBean()
    {
        final Object bean = new Object();
        Mockito.when( this.container.getComponentInstance( "mybean" ) ).thenReturn( bean );

        this.manager.addContainer( this.serviceRef );
        final Object lookedup = this.manager.getBean( ModuleKey.from( "foo.bar" ), "mybean" );

        Assert.assertSame( bean, lookedup );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBean_removeContainer()
    {
        final Object bean = new Object();
        Mockito.when( this.container.getComponentInstance( "mybean" ) ).thenReturn( bean );

        this.manager.addContainer( this.serviceRef );
        final Object lookedup = this.manager.getBean( ModuleKey.from( "foo.bar" ), "mybean" );

        Assert.assertSame( bean, lookedup );

        this.manager.removeContainer( this.serviceRef );
        this.manager.getBean( ModuleKey.from( "foo.bar" ), "mybean" );
    }
}
