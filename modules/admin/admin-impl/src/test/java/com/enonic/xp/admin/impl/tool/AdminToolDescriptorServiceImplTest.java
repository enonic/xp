package com.enonic.xp.admin.impl.tool;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.admin.tool.AdminToolDescriptors;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public class AdminToolDescriptorServiceImplTest
    extends ApplicationTestSupport
{
    protected AdminToolDescriptorServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        this.service = new AdminToolDescriptorServiceImpl();
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
        AdminToolDescriptors result = this.service.getAllowedAdminToolDescriptors( principalKeys );
        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );

        result = this.service.getAllowedAdminToolDescriptors( PrincipalKeys.empty() );
        Assert.assertNotNull( result );
        Assert.assertEquals( 0, result.getSize() );
    }
}
