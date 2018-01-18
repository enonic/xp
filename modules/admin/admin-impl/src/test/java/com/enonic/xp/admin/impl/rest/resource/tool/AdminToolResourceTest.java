package com.enonic.xp.admin.impl.rest.resource.tool;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.admin.tool.AdminToolDescriptors;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.RoleKeys;

public class AdminToolResourceTest
    extends AdminResourceTestSupport
{
    private AdminToolDescriptorService adminToolDescriptorService;

    private LocaleService localeService;

    @Override
    protected Object getResourceInstance()
    {
        this.adminToolDescriptorService = Mockito.mock( AdminToolDescriptorService.class );
        this.localeService = Mockito.mock( LocaleService.class );

        final AdminToolResource resource = new AdminToolResource();
        resource.setAdminToolDescriptorService( this.adminToolDescriptorService );
        resource.setLocaleService( this.localeService );
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
        Mockito.when( this.adminToolDescriptorService.getAllowedAdminToolDescriptors( Mockito.any() ) ).thenReturn( list );

        final String response = request().path( "tool/list" ).get().getAsString();
        assertJson( "getAllowedAdminToolDescriptors.json", response );
    }

    @Test
    public void getAllowedAdminToolDescriptors_empty()
        throws Exception
    {
        final AdminToolDescriptors list = AdminToolDescriptors.empty();
        Mockito.when( this.adminToolDescriptorService.getAllowedAdminToolDescriptors( Mockito.any() ) ).thenReturn( list );

        final String response = request().path( "tool/list" ).get().getAsString();
        assertJson( "getAllowedAdminToolDescriptors_empty.json", response );
    }

    @Test
    public void getAllowedAdminToolDescriptors_i18n()
        throws Exception
    {
        final AdminToolDescriptor desc1 = AdminToolDescriptor.create().
            key( DescriptorKey.from( "myapp:desc1" ) ).
            displayName( "Display Name desc1" ).
            displayNameI18nKey( "key.display-name" ).
            description( "Description desc1" ).
            displayNameI18nKey( "key.description" ).
            addAllowedPrincipals( RoleKeys.ADMIN ).
            addAllowedPrincipals( RoleKeys.AUTHENTICATED ).
            build();

        final AdminToolDescriptors list = AdminToolDescriptors.from( desc1 );
        Mockito.when( this.adminToolDescriptorService.getAllowedAdminToolDescriptors( Mockito.any() ) ).thenReturn( list );

        final MessageBundle messageBundle = Mockito.mock( MessageBundle.class );
        Mockito.when( messageBundle.localize( "key.display-name" ) ).thenReturn( "translated.displayName" );
        Mockito.when( messageBundle.localize( "key.description" ) ).thenReturn( "translated.description" );

        Mockito.when( this.localeService.getBundle( Mockito.any(), Mockito.any() ) ).thenReturn( messageBundle );

        final String response = request().path( "tool/list" ).get().getAsString();
        assertJson( "getAllowedAdminToolDescriptors_i18n.json", response );
    }
}
