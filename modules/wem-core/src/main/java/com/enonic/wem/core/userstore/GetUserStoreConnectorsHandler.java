package com.enonic.wem.core.userstore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.userstore.GetUserStoreConnectors;
import com.enonic.wem.api.userstore.connector.UserStoreConnector;
import com.enonic.wem.api.userstore.connector.UserStoreConnectors;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

import com.enonic.cms.core.security.userstore.UserStoreConnectorManager;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;

@Component
public class GetUserStoreConnectorsHandler
    extends CommandHandler<GetUserStoreConnectors>
{

    private UserStoreConnectorManager userStoreConnectorManager;

    public GetUserStoreConnectorsHandler()
    {
        super( GetUserStoreConnectors.class );
    }

    @Override
    public void handle( final CommandContext context, final GetUserStoreConnectors command )
        throws Exception
    {
        final Map<String, UserStoreConnectorConfig> connectorsConfig = userStoreConnectorManager.getUserStoreConnectorConfigs();
        final List<UserStoreConnector> connectors = new ArrayList<UserStoreConnector>();
        for ( UserStoreConnectorConfig connectorConfig : connectorsConfig.values() )
        {
            connectors.add( buildUserStoreConnector( connectorConfig ) );
        }
        command.setResult( UserStoreConnectors.from( connectors ) );
    }


    private UserStoreConnector buildUserStoreConnector( UserStoreConnectorConfig connectorConfig )
    {
        final  UserStoreConnector connector = new UserStoreConnector( connectorConfig.getName() );
        connector.setCreateGroup( connectorConfig.canCreateGroup() );
        connector.setCreateUser( connectorConfig.canCreateUser() );
        connector.setDeleteGroup( connectorConfig.canDeleteGroup() );
        connector.setDeleteUser( connectorConfig.canDeleteUser() );
        connector.setGroupsStoredRemote( connectorConfig.groupsStoredRemote() );
        connector.setPluginClass( connectorConfig.getPluginType() );
        connector.setReadGroup( connectorConfig.canReadGroup() );
        connector.setResurrectDeletedGroups( connectorConfig.resurrectDeletedGroups() );
        connector.setResurrectDeletedUsers( connectorConfig.resurrectDeletedUsers() );
        connector.setUpdateGroup( connectorConfig.canUpdateGroup() );
        connector.setUpdatePassword( connectorConfig.canUpdateUserPassword() );
        connector.setUpdateUser( connectorConfig.canUpdateUser() );
        return connector;
    }

    @Autowired
    public void setUserStoreConnectorManager( final UserStoreConnectorManager userStoreConnectorManager )
    {
        this.userStoreConnectorManager = userStoreConnectorManager;
    }
}
