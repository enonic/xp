package com.enonic.xp.core.impl.content.page.layout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Applications;
import com.enonic.xp.core.impl.content.page.AbstractDescriptorServiceTest;
import com.enonic.xp.core.impl.content.page.region.LayoutDescriptorServiceImpl;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.resource.ResourceKey;

public class LayoutDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    protected LayoutDescriptorServiceImpl service;

    @Before
    public final void setupService()
    {
        this.service = new LayoutDescriptorServiceImpl();
        this.service.setResourceService( this.resourceService );
        this.service.setMixinService( this.mixinService );
    }

    @Override
    protected final ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), "site/layouts/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    @Override
    protected final String toDescriptorXml( final DescriptorKey key )
    {
        return "<layout><display-name>" + key.getName() + "</display-name></layout>";
    }

    @Test
    public void testGetByKey()
        throws Exception
    {
        final DescriptorKey key = createDescriptor( "myapp:mylayout" );
        final LayoutDescriptor descriptor = this.service.getByKey( key );
        Assert.assertNotNull( descriptor );
    }

    @Test
    public void testGetByApplication()
        throws Exception
    {
        final Application application = createApplication( "myapp" );
        createDescriptors( "myapp:mylayout" );

        mockFindFiles( application, "/site/layouts", "/site/layouts/mylayout/mylayout.xml" );

        final LayoutDescriptors result = this.service.getByApplication( application.getKey() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 1, result.getSize() );
    }

    @Test
    public void testGetByApplications()
        throws Exception
    {
        final Applications applications = createApplications( "myapp1", "myapp2" );
        createDescriptors( "myapp1:mylayout", "myapp2:mylayout" );

        mockFindFiles( applications.getApplication( ApplicationKey.from( "myapp1" ) ), "/site/layouts",
                       "/site/layouts/mylayout/mylayout.xml" );
        mockFindFiles( applications.getApplication( ApplicationKey.from( "myapp2" ) ), "/site/layouts",
                       "/site/layouts/mylayout/mylayout.xml" );

        final LayoutDescriptors result = this.service.getByApplications( applications.getApplicationKeys() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );
    }
}
