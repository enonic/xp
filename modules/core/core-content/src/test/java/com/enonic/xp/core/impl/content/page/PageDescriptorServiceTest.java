package com.enonic.xp.core.impl.content.page;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Applications;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.resource.ResourceKey;

public class PageDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    protected PageDescriptorServiceImpl service;

    @Before
    public final void setupService()
    {
        this.service = new PageDescriptorServiceImpl();
        this.service.setResourceService( this.resourceService );
        this.service.setMixinService( this.mixinService );
    }

    @Override
    protected final ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), "site/pages/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    @Override
    protected final String toDescriptorXml( final DescriptorKey key )
    {
        return "<page><display-name>" + key.getName() + "</display-name></page>";
    }

    @Test
    public void testGetByKey()
        throws Exception
    {
        final DescriptorKey key = createDescriptor( "myapp:mypage" );
        final PageDescriptor descriptor = this.service.getByKey( key );
        Assert.assertNotNull( descriptor );
    }

    @Test
    public void testGetByApplication()
        throws Exception
    {
        final Application application = createApplication( "myapp" );
        createDescriptors( "myapp:mypage" );

        mockFindFolders( application, "/site/pages", "/site/pages/mypage" );

        final PageDescriptors result = this.service.getByApplication( application.getKey() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 1, result.getSize() );
    }

    @Test
    public void testGetByApplications()
        throws Exception
    {
        final Applications applications = createApplications( "myapp1", "myapp2" );
        createDescriptors( "myapp1:mypage", "myapp2:mypage" );

        mockFindFolders( applications.getApplication( ApplicationKey.from( "myapp1" ) ), "/site/pages", "/site/pages/mypage" );
        mockFindFolders( applications.getApplication( ApplicationKey.from( "myapp2" ) ), "/site/pages", "/site/pages/mypage" );

        final PageDescriptors result = this.service.getByApplications( applications.getApplicationKeys() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );
    }
}
