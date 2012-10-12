package com.enonic.wem.migrate.jcr;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.account.CreateAccount;
import com.enonic.wem.api.command.account.UpdateAccounts;
import com.enonic.wem.api.command.userstore.CreateUserStore;
import com.enonic.wem.api.command.userstore.UpdateUserStores;

public class JcrAccountsImporterTest
{
    @Autowired
    private Client client;

    private JcrAccountsImporter jcrAccountsImporter;

    private MockDatabaseAccountsLoader databaseAccountsLoader;

    @Before
    public void setUp()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        databaseAccountsLoader = new MockDatabaseAccountsLoader();

        jcrAccountsImporter = new JcrAccountsImporter();
        jcrAccountsImporter.setClient( client );
        jcrAccountsImporter.setDbAccountsLoader( databaseAccountsLoader );
    }

    @Test
    public void testImport()
    {
        jcrAccountsImporter.importAccounts();

        Mockito.verify( client, Mockito.times( 1) ).execute( Matchers.isA( UpdateUserStores.class ) ); // update system user store
        Mockito.verify( client, Mockito.times( 1) ).execute( Matchers.isA( CreateUserStore.class) );
        Mockito.verify( client, Mockito.times( 7) ).execute( Matchers.isA( CreateAccount.class ) );

        // set members for group Enonic Employees
        Mockito.verify( client, Mockito.times( 1) ).execute( Matchers.isA( UpdateAccounts.class ) );
    }

}
