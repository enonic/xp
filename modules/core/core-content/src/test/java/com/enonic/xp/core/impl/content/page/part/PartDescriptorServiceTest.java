package com.enonic.xp.core.impl.content.page.part;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Applications;
import com.enonic.xp.core.impl.content.page.AbstractDescriptorServiceTest;
import com.enonic.xp.core.impl.content.page.region.PartDescriptorServiceImpl;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptors;
import com.enonic.xp.resource.ResourceKey;

public class PartDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    protected PartDescriptorServiceImpl service;

    @Before
    public final void setupService()
    {
        this.service = new PartDescriptorServiceImpl();
        this.service.setResourceService( this.resourceService );
        this.service.setMixinService( this.mixinService );
    }

    @Override
    protected final ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), "site/parts/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    @Override
    protected final String toDescriptorXml( final DescriptorKey key )
    {
        return "<part><display-name>" + key.getName() + "</display-name></part>";
    }

    @Test
    public void testGetByKey()
        throws Exception
    {
        final DescriptorKey key = createDescriptor( "myapp:mypart" );
        final PartDescriptor descriptor = this.service.getByKey( key );
        Assert.assertNotNull( descriptor );
    }

    @Test
    public void testGetByApplication()
        throws Exception
    {
        final Application application = createApplication( "myapp" );
        createDescriptors( "myapp:mypart" );

        mockFindFolders( application, "/site/parts", "/site/parts/mypart" );

        final PartDescriptors result = this.service.getByApplication( application.getKey() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 1, result.getSize() );
    }

    @Test
    public void testGetByApplications()
        throws Exception
    {
        final Applications applications = createApplications( "myapp1", "myapp2" );
        createDescriptors( "myapp1:mypart", "myapp2:mypart" );

        mockFindFolders( applications.getApplication( ApplicationKey.from( "myapp1" ) ), "/site/parts", "/site/parts/mypart" );
        mockFindFolders( applications.getApplication( ApplicationKey.from( "myapp2" ) ), "/site/parts", "/site/parts/mypart" );

        final PartDescriptors result = this.service.getByApplications( applications.getApplicationKeys() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );
    }
}
