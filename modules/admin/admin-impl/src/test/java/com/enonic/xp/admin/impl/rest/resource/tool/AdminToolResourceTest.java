package com.enonic.xp.admin.impl.rest.resource.tool;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.admin.tool.AdminToolDescriptors;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.RoleKeys;

public class AdminToolResourceTest
    extends AdminResourceTestSupport
{
    private AdminToolDescriptorService service;

    @Override
    protected Object getResourceInstance()
    {
        this.service = Mockito.mock( AdminToolDescriptorService.class );

        final AdminToolResource resource = new AdminToolResource();
        resource.setAdminToolDescriptorService( this.service );
        return resource;
    }

    @Test
    public void getAllowedAdminToolDescriptors()
        throws Exception
    {
        final AdminToolDescriptor desc1 = AdminToolDescriptor.create().
            key( DescriptorKey.from( "myapp:desc1" ) ).
            displayName( "Display Name desc1" ).
            description( "Description desc1" ).
            addAllowedPrincipals( RoleKeys.ADMIN ).
            addAllowedPrincipals( RoleKeys.AUTHENTICATED ).
            build();

        final AdminToolDescriptor desc2 = AdminToolDescriptor.create().
            key( DescriptorKey.from( "myapp:desc2" ) ).
            displayName( "Display Name desc2" ).
            description( "Description desc2" ).
            addAllowedPrincipals( RoleKeys.ADMIN ).
            build();

        final AdminToolDescriptors list = AdminToolDescriptors.from( desc1, desc2 );
        Mockito.when( this.service.getAllowedAdminToolDescriptors( Mockito.any() ) ).thenReturn( list );

        final String response = request().path( "tool/list" ).get().getAsString();
        assertJson( "getAllowedAdminToolDescriptors.json", response );
    }

    @Test
    public void getAllowedAdminToolDescriptors_empty()
        throws Exception
    {
        final AdminToolDescriptors list = AdminToolDescriptors.empty();
        Mockito.when( this.service.getAllowedAdminToolDescriptors( Mockito.any() ) ).thenReturn( list );

        final String response = request().path( "tool/list" ).get().getAsString();
        assertJson( "getAllowedAdminToolDescriptors_empty.json", response );
    }
}
