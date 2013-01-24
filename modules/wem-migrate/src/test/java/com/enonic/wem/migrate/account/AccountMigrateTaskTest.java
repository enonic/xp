package com.enonic.wem.migrate.account;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.account.CreateAccount;
import com.enonic.wem.api.command.account.UpdateAccounts;
import com.enonic.wem.api.command.userstore.CreateUserStore;
import com.enonic.wem.api.command.userstore.UpdateUserStores;

public class AccountMigrateTaskTest
{
    private Client client;

    private AccountMigrateTask jcrAccountsImporter;

    private MockDatabaseAccountsLoader databaseAccountsLoader;

    @Before
    public void setUp()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        databaseAccountsLoader = new MockDatabaseAccountsLoader();

        jcrAccountsImporter = new AccountMigrateTask();
        jcrAccountsImporter.setClient( client );
        jcrAccountsImporter.setDbAccountsLoader( databaseAccountsLoader );
    }

    @Test
    public void testMigrate()
        throws Exception
    {
        jcrAccountsImporter.migrate();

        Mockito.verify( client, Mockito.times( 1 ) ).execute( Matchers.isA( UpdateUserStores.class ) ); // update system user store
        Mockito.verify( client, Mockito.times( 1 ) ).execute( Matchers.isA( CreateUserStore.class ) );
        Mockito.verify( client, Mockito.times( 7 ) ).execute( Matchers.isA( CreateAccount.class ) );

        // set members for group Enonic Employees
        Mockito.verify( client, Mockito.times( 1 ) ).execute( Matchers.isA( UpdateAccounts.class ) );
    }
}
