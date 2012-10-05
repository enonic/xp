package com.enonic.wem.core.userstore;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.connector.UserStoreConnector;
import com.enonic.wem.core.command.CommandHandler;

import com.enonic.cms.core.security.userstore.UserStoreConnectorManager;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.store.dao.UserStoreDao;

public abstract class AbstractUserStoreHandler<T extends Command>
    extends CommandHandler<T>
{

    protected UserStoreDao userStoreDao;

    protected UserStoreConnectorManager userStoreConnectorManager;


    public AbstractUserStoreHandler( final Class<T> type )
    {
        super( type );
    }


    protected UserStoreConnector getUserStoreConnector( final UserStoreName userStoreName )
    {
        final UserStoreEntity userStoreEntity = userStoreDao.findByName( userStoreName.toString() );
        if ( ( userStoreEntity == null ) || userStoreEntity.isLocal() )
        {
            return null;
        }
        return getUserStoreConnector( userStoreEntity );
    }

    protected UserStoreConnector getUserStoreConnector( final UserStoreEntity userStoreEntity )
    {
        final UserStoreConnectorConfig connectorConfig = userStoreConnectorManager.getUserStoreConnectorConfig( userStoreEntity.getKey() );
        final UserStoreConnector connector = new UserStoreConnector( connectorConfig.getName() );
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
    public void setUserStoreDao( final UserStoreDao userStoreDao )
    {
        this.userStoreDao = userStoreDao;
    }

    @Autowired
    public void setUserStoreConnectorManager( final UserStoreConnectorManager userStoreConnectorManager )
    {
        this.userStoreConnectorManager = userStoreConnectorManager;
    }
}
