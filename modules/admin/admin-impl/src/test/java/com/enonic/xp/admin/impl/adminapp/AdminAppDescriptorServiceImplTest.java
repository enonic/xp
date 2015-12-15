package com.enonic.xp.admin.impl.adminapp;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.admin.adminapp.AdminApplicationDescriptors;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public class AdminAppDescriptorServiceImplTest
    extends ApplicationTestSupport
{
    protected AdminApplicationDescriptorServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        this.service = new AdminApplicationDescriptorServiceImpl();
        this.service.setApplicationService( this.applicationService );
        this.service.setResourceService( this.resourceService );

        addApplication( "myapp1", "/apps/myapp1" );
        addApplication( "myapp2", "/apps/myapp2" );
    }

    @Test
    public void getByInterface()
        throws Exception
    {
        final PrincipalKeys principalKeys = PrincipalKeys.from( PrincipalKey.from( "role:system.admin" ) );
        AdminApplicationDescriptors result = this.service.getAllowedAdminApplicationDescriptors( principalKeys );
        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );

        result = this.service.getAllowedAdminApplicationDescriptors( PrincipalKeys.empty() );
        Assert.assertNotNull( result );
        Assert.assertEquals( 0, result.getSize() );
    }
}
