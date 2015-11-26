package com.enonic.xp.admin.impl.app;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.admin.app.AdminApplicationDescriptors;
import com.enonic.xp.admin.impl.widget.AbstractDescriptorServiceTest;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Applications;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;

public class AdminApplicationDescriptorServiceImpl_getAll
    extends AbstractDescriptorServiceTest
{
    private AdminApplicationDescriptorServiceImpl service;

    @Before
    public final void setupService()
    {
        this.service = new AdminApplicationDescriptorServiceImpl();
        this.service.setApplicationService( this.applicationService );
        this.service.setResourceService( this.resourceService );
    }

    @Test
    public void getAll()
        throws Exception
    {
        final Applications applications = createApplications( "foomodule", "barmodule" );
        createDescriptors( "foomodule:foomodule-adminapp-descr", "barmodule:barmodule-adminapp-descr" );

        mockResources( applications.getApplication( ApplicationKey.from( "foomodule" ) ), "/admin/app/",
                       "admin/app/foomodule-adminapp-descr" );
        mockResources( applications.getApplication( ApplicationKey.from( "barmodule" ) ), "/admin/app/",
                       "admin/app/barmodule-adminapp-descr" );

        AdminApplicationDescriptors result = this.service.getAll();
        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );
    }

    @Override
    protected final ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), "/admin/app/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    @Override
    protected final String toDescriptorXml( final DescriptorKey key )
    {
        return "<admin-application>" +
            "<name>" + key.getName() + "</name>" +
            "<short-name>" + key.getName() + "</short-name>" +
            "<icon>default</icon>" +
            "<allow>" +
            "<principal>role:system.admin</principal>" +
            "<principal>role:system.everyone</principal>" +
            "</allow>" +
            "</admin-application>";
    }
}
