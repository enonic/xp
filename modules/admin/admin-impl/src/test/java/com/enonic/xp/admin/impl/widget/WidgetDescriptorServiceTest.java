package com.enonic.xp.admin.impl.widget;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.admin.widget.WidgetDescriptors;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;

public class WidgetDescriptorServiceTest
    extends ApplicationTestSupport
{
    protected WidgetDescriptorServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        this.service = new WidgetDescriptorServiceImpl();
        this.service.setApplicationService( this.applicationService );
        this.service.setResourceService( this.resourceService );

        addApplication( "myapp1", "/apps/myapp1" );
        addApplication( "myapp2", "/apps/myapp2" );
    }

    @Test
    public void getByInterface()
        throws Exception
    {
        WidgetDescriptors result = this.service.getByInterfaces( "com.enonic.xp.my-interface" );
        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );

        result = this.service.getByInterfaces( "com.enonic.xp.unknown-interface" );
        Assert.assertNotNull( result );
        Assert.assertEquals( 0, result.getSize() );
    }
}
