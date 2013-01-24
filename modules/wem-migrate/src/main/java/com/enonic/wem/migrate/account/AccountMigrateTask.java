package com.enonic.wem.migrate.account;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.config.UserStoreConfig;
import com.enonic.wem.api.userstore.config.UserStoreConfigParser;
import com.enonic.wem.migrate.jdbc.JdbcMigrateTask;
import com.enonic.wem.migrate.jdbc.JdbcRowCallback;

import static com.enonic.wem.api.command.Commands.userStore;
import static com.enonic.wem.api.userstore.editor.UserStoreEditors.setUserStore;

@Component
public final class AccountMigrateTask
    extends JdbcMigrateTask
{
    private final Map<Integer, String> userStoreKeyName;

    private Client client;

    public AccountMigrateTask()
    {
        this.userStoreKeyName = new HashMap<>();
    }

    @Autowired
    public void setClient( final Client client )
    {
        this.client = client;
    }

    @Override
    public void migrate()
        throws Exception
    {
        this.userStoreKeyName.clear();

        importUserStores();
        importUsers();
        importGroups();
        importMemberships();
        setUserStoreAdministrators();
    }

    private void importUserStores()
        throws Exception
    {
        query( "SELECT * FROM tDomain WHERE dom_bIsDeleted = 0", new JdbcRowCallback()
        {
            @Override
            public void onRow( final ResultSet result )
                throws Exception
            {
                importUserStore( result );
            }
        } );
    }

    private void importUsers()
        throws Exception
    {
        query( "SELECT * FROM tUser WHERE usr_bIsDeleted = 0", new JdbcRowCallback()
        {
            @Override
            public void onRow( final ResultSet result )
                throws Exception
            {
                importUser( result );
            }
        } );
    }

    private void importGroups()
        throws Exception
    {
        query( "SELECT * FROM tGroup WHERE grp_bIsDeleted = 0", new JdbcRowCallback()
        {
            @Override
            public void onRow( final ResultSet result )
                throws Exception
            {
                importGroup( result );
            }
        } );
    }

    private void importMemberships()
        throws Exception
    {
        query( "SELECT * FROM tGrpGrpMembership", new JdbcRowCallback()
        {
            @Override
            public void onRow( final ResultSet result )
                throws Exception
            {
                importMembership( result );
            }
        } );
    }

    private void importUserStore( final ResultSet result )
        throws Exception
    {
        final int key = result.getInt( "dom_lKey" );
        final String name = result.getString( "dom_sName" );
        final boolean defaultStore = result.getInt( "dom_bDefaultStore" ) == 1;
        final String connectorName = result.getString( "dom_sConfigName" );
        final byte[] xmlDataBytes = result.getBytes( "dom_xmlData" );
        final String xmlDataString = xmlDataBytes == null ? null : new String( xmlDataBytes, "UTF-8" );

        final UserStore userStore = new UserStore( UserStoreName.from( name ) );
        userStore.setDefaultStore( defaultStore );

        final UserStoreConfig config;
        if ( Strings.isNullOrEmpty( xmlDataString ) )
        {
            config = new UserStoreConfig();
        }
        else
        {
            config = new UserStoreConfigParser().parseXml( xmlDataString );
        }

        userStore.setConfig( config );
        userStore.setConnectorName( connectorName );

        if ( userStore.getName().isSystem() )
        {
            this.client.execute(
                userStore().update().editor( setUserStore( userStore ) ).names( UserStoreNames.from( userStore.getName() ) ) );
        }
        else
        {
            this.client.execute( userStore().create().userStore( userStore ) );
        }

        this.userStoreKeyName.put( key, name );
        this.logger.info( "UserStore imported: " + name );
    }

    private void importUser( final ResultSet result )
        throws Exception
    {
        // TODO: Implement
    }

    private void importGroup( final ResultSet result )
        throws Exception
    {
        // TODO: Implement
    }

    private void importMembership( final ResultSet result )
        throws Exception
    {
        // TODO: Implement
    }

    private void setUserStoreAdministrators()
        throws Exception
    {
    }
}

