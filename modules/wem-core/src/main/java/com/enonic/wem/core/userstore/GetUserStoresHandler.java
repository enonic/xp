package com.enonic.wem.core.userstore;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.userstore.GetUserStores;
import com.enonic.wem.api.exception.UserStoreNotFoundException;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.UserStores;
import com.enonic.wem.api.userstore.config.UserStoreConfig;
import com.enonic.wem.api.userstore.config.UserStoreFieldConfig;
import com.enonic.wem.api.userstore.connector.UserStoreConnector;
import com.enonic.wem.api.userstore.statistics.UserStoreStatistics;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.userstore.UserStoreConnectorManager;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserStoreDao;

@Component
public class GetUserStoresHandler
    extends CommandHandler<GetUserStores>
{

    private UserStoreDao userStoreDao;

    private GroupDao groupDao;

    private UserStoreConnectorManager userStoreConnectorManager;

    private UserStoreService userStoreService;

    public GetUserStoresHandler()
    {
        super( GetUserStores.class );
    }

    @Override
    public void handle( final CommandContext context, final GetUserStores command )
        throws Exception
    {
        final boolean includeConfig = command.isIncludeConfig();
        final boolean includeConnector = command.isIncludeConnector();
        final boolean includeStatistics = command.isIncludeStatistics();
        final UserStoreNames userStoreNames = command.getNames();

        final List<UserStore> userStoreList = Lists.newArrayList();
        for ( UserStoreName name : userStoreNames )
        {
            UserStore userStore = fetchUserStore( name, includeConfig, includeConnector, includeStatistics );
            userStoreList.add( userStore );
        }

        command.setResult( UserStores.from( userStoreList ) );
    }

    private UserStore fetchUserStore( final UserStoreName name, final boolean includeConfig, final boolean includeConnector,
                                      final boolean includeStatistics )
    {
        final UserStoreEntity userStoreEntity = userStoreDao.findByName( name.toString() );
        if ( userStoreEntity == null )
        {
            throw new UserStoreNotFoundException( name );
        }

        final UserStore userStore = new UserStore( name );
        userStore.setConnectorName( userStoreEntity.getConnectorName() );
        userStore.setDefaultStore( userStoreEntity.isDefaultUserStore() );

        if ( includeConnector && userStoreEntity.isRemote() )
        {
            userStore.setConnector( getUserStoreConnector( userStoreEntity ) );
        }
        if ( includeConfig )
        {
            userStore.setConfig( getUserStoreConfig( userStoreEntity.getConfig() ) );
        }
        if ( includeStatistics )
        {
            userStore.setStatistics( getStatistics( userStoreEntity ) );
        }

        userStore.setAdministrators( getUserStoreAdministrators( userStoreEntity ) );

        return userStore;
    }

    private AccountKeys getUserStoreAdministrators( final UserStoreEntity userStoreEntity )
    {
        final GroupEntity builtInUserStoreAdministrator = groupDao.findBuiltInUserStoreAdministrator( userStoreEntity.getKey() );
        final Set<GroupEntity> userStoreAdmins = builtInUserStoreAdministrator.getMembers( false );

        final List<AccountKey> adminAccounts = Lists.newArrayList();
        final String userStoreName = userStoreEntity.getName();
        for ( GroupEntity groupEntity : userStoreAdmins )
        {
            if ( groupEntity.getUser() != null )
            {
                adminAccounts.add( AccountKey.user( userStoreName + ":" + groupEntity.getUser().getName() ) );
            }
        }

        return AccountKeys.from( adminAccounts );
    }

    private UserStoreStatistics getStatistics( final UserStoreEntity userStoreEntity )
    {
        final int numUsers = userStoreService.getUsers( userStoreEntity.getKey() ).size();

        final GroupSpecification getAllGroupsSpec = new GroupSpecification();
        getAllGroupsSpec.setDeletedState( GroupSpecification.DeletedState.NOT_DELETED );
        getAllGroupsSpec.setUserStoreKey( userStoreEntity.getKey() );
        getAllGroupsSpec.setType( GroupType.USERSTORE_GROUP );
        final int numGroups = userStoreService.getGroups( getAllGroupsSpec ).size();

        final Collection<GroupType> builtInTypes = GroupType.getBuiltInTypes();
        int numRoles = 0;
        for ( GroupType builtInType : builtInTypes )
        {
            final GroupSpecification getAllGroupsSpecRole = new GroupSpecification();
            getAllGroupsSpecRole.setDeletedState( GroupSpecification.DeletedState.NOT_DELETED );
            getAllGroupsSpecRole.setUserStoreKey( userStoreEntity.getKey() );
            getAllGroupsSpecRole.setType( builtInType );
            numRoles += userStoreService.getGroups( getAllGroupsSpecRole ).size();
        }

        final UserStoreStatistics statistics = new UserStoreStatistics();
        statistics.setNumGroups( numGroups );
        statistics.setNumUsers( numUsers );
        statistics.setNumRoles( numRoles );
        return statistics;
    }

    private UserStoreConfig getUserStoreConfig( final com.enonic.cms.core.security.userstore.config.UserStoreConfig userStoreEntityConfig )
    {
        final UserStoreConfig userStoreConfig = new UserStoreConfig();
        for ( UserStoreUserFieldConfig userFieldConfig : userStoreEntityConfig.getUserFieldConfigs() )
        {
            final UserStoreFieldConfig field = new UserStoreFieldConfig( userFieldConfig.getType().getName() );
            field.setIso( userFieldConfig.useIso() );
            field.setReadOnly( userFieldConfig.isReadOnly() );
            field.setRemote( userFieldConfig.isRemote() );
            field.setRequired( userFieldConfig.isRequired() );
            userStoreConfig.addField( field );
        }
        return userStoreConfig;
    }

    private UserStoreConnector getUserStoreConnector( final UserStoreEntity userStoreEntity )
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

    @Autowired
    public void setUserStoreService( final UserStoreService userStoreService )
    {
        this.userStoreService = userStoreService;
    }

    @Autowired
    public void setGroupDao( final GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }
}
