package com.enonic.xp.admin.impl.tool;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptors;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static org.junit.Assert.*;

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
    public void getAllowedAdminToolDescriptors()
        throws Exception
    {
        final PrincipalKeys principalKeys = PrincipalKeys.from( PrincipalKey.from( "role:system.user.admin" ) );
        AdminToolDescriptors result = this.service.getAllowedAdminToolDescriptors( principalKeys );
        Assert.assertNotNull( result );
        Assert.assertEquals( 1, result.getSize() );

        result = this.service.getAllowedAdminToolDescriptors( PrincipalKeys.empty() );
        Assert.assertNotNull( result );
        Assert.assertEquals( 0, result.getSize() );
    }

    @Test
    public void generateAdminToolUri()
    {
        final String uri = this.service.generateAdminToolUri( ApplicationKey.from( "myapp1" ).toString(), "myToolName" );
        assertEquals( "/admin/tool/myapp1/myToolName", uri );
    }

    @Test
    public void getHomeToolUri()
    {
        final String uri = this.service.getHomeToolUri();
        assertEquals( "/admin/tool", uri );
    }

    @Test
    public void getByApplication()
    {
        final AdminToolDescriptors result = this.service.getByApplication( ApplicationKey.from( "myapp1" ) );

        assertEquals( 1, result.getSize() );

        final AdminToolDescriptor adminToolDescriptor = result.get( 0 );

        Assert.assertEquals( "My admin tool", adminToolDescriptor.getDisplayName() );
        Assert.assertEquals( "My admin tool description", adminToolDescriptor.getDescription() );
        Assert.assertEquals( 1, adminToolDescriptor.getAllowedPrincipals().getSize() );
    }


    @Test
    public void getByKey()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( ApplicationKey.from( "myapp1" ), "myadmintool" );
        final AdminToolDescriptor result = this.service.getByKey( descriptorKey );

        Assert.assertNotNull( result );
        Assert.assertEquals( "My admin tool", result.getDisplayName() );
        Assert.assertEquals( "My admin tool description", result.getDescription() );
        Assert.assertEquals( 1, result.getAllowedPrincipals().getSize() );
    }

    @Test
    public void getIconByKey()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( ApplicationKey.from( "myapp1" ), "myadmintool" );
        final String icon = this.service.getIconByKey( descriptorKey );

        Assert.assertNull( icon );

        final DescriptorKey descriptorKey2 = DescriptorKey.from( ApplicationKey.from( "myapp2" ), "myadmintool" );
        final String icon2 = this.service.getIconByKey( descriptorKey2 );

        Assert.assertEquals( "<svg>SVG content</svg>", icon2 );
    }
}
