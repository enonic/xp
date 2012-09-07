package com.enonic.wem.web.data.binary;

import java.awt.image.BufferedImage;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.command.account.GetAccounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AccountImageControllerTest
{
    private AccountImageController controller;

    @Before
    public void setUp()
        throws Exception
    {
        this.controller = new AccountImageController();
    }

    private void setupAccount( final Account account )
    {
        final List<Account> list = Lists.newArrayList();

        if ( account != null )
        {
            list.add( account );
        }

        final Accounts result = Accounts.from( list );
        final Client client = Mockito.mock( Client.class );
        Mockito.when( client.execute( Mockito.any( GetAccounts.class ) ) ).thenReturn( result );

        this.controller.setClient( client );
    }

    @Test
    public void testAccountImage()
        throws Exception
    {
        final byte[] data = Resources.toByteArray( getClass().getResource( "user.png" ) );

        final UserAccount account = UserAccount.create( "other:dummy" );
        account.setImage( data );

        setupAccount( account );
        assertImage( this.controller.getAccountImage( "user:other:dummy", 20 ), 20 );
    }

    @Test
    public void testAccountImage_notFound()
        throws Exception
    {
        setupAccount( null );
        assertNull( this.controller.getAccountImage( "user:other:dummy", 10 ) );

        setupAccount( UserAccount.create( "other:dummy" ) );
        assertNull( this.controller.getAccountImage( "user:other:dummy", 20 ) );

        setupAccount( GroupAccount.create( "other:dummy" ) );
        assertNull( this.controller.getAccountImage( "group:other:dummy", 30 ) );

        setupAccount( RoleAccount.create( "other:dummy" ) );
        assertNull( this.controller.getAccountImage( "role:other:dummy", 40 ) );
    }

    @Test
    public void testDefaultImage()
        throws Exception
    {
        assertImage( this.controller.getDefaultImage( "admin", 10 ), 10 );
        assertImage( this.controller.getDefaultImage( "anonymous", 20 ), 20 );
        assertImage( this.controller.getDefaultImage( "group", 30 ), 30 );
        assertImage( this.controller.getDefaultImage( "role", 40 ), 40 );
        assertImage( this.controller.getDefaultImage( "user", 50 ), 50 );
    }

    @Test
    public void testDefaultImage_notFound()
        throws Exception
    {
        assertNull( this.controller.getDefaultImage( "other", 10 ) );
    }

    private void assertImage( final BufferedImage image, final int size )
    {
        assertNotNull( image );
        assertEquals( size, image.getWidth() );
    }
}
