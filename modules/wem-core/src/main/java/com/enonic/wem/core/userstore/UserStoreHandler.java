package com.enonic.wem.core.userstore;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.userstore.config.UserStoreConfig;
import com.enonic.wem.api.userstore.config.UserStoreFieldConfig;
import com.enonic.wem.api.userstore.connector.UserStoreConnector;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.search.account.AccountIndexData;
import com.enonic.wem.core.search.account.AccountIndexDataEntity;
import com.enonic.wem.core.search.account.AccountSearchService;
import com.enonic.wem.core.search.account.Group;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.group.UpdateGroupCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreConnectorManager;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.core.user.field.UserFieldType;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

public abstract class UserStoreHandler<T extends Command>
    extends CommandHandler<T>
{

    protected UserDao userDao;

    protected UserStoreDao userStoreDao;

    protected UserStoreService userStoreService;

    protected SecurityService securityService;

    protected AccountSearchService searchService;

    protected GroupDao groupDao;

    protected UserStoreConnectorManager userStoreConnectorManager;


    public UserStoreHandler( final Class<T> type )
    {
        super( type );
    }

    protected com.enonic.cms.core.security.userstore.config.UserStoreConfig convertToOldConfig( UserStoreConfig config )
    {
        com.enonic.cms.core.security.userstore.config.UserStoreConfig oldConfig =
            new com.enonic.cms.core.security.userstore.config.UserStoreConfig();
        for ( UserStoreFieldConfig fieldConfig : config )
        {
            oldConfig.addUserFieldConfig( convertToOldFieldConfig( fieldConfig ) );
        }
        return oldConfig;
    }

    protected UserStoreUserFieldConfig convertToOldFieldConfig( UserStoreFieldConfig fieldConfig )
    {
        UserStoreUserFieldConfig oldFieldConfig = new UserStoreUserFieldConfig( UserFieldType.fromName( fieldConfig.getName() ) );
        oldFieldConfig.setIso( fieldConfig.isIso() );
        oldFieldConfig.setReadOnly( fieldConfig.isReadOnly() );
        oldFieldConfig.setRemote( fieldConfig.isReadOnly() );
        oldFieldConfig.setRequired( fieldConfig.isRequired() );
        return oldFieldConfig;
    }

    protected GroupEntity getGroupEntity( AccountKey accountKey )
    {
        UserStoreEntity userStoreEntity = userStoreDao.findByName( accountKey.getUserStore() );
        if ( accountKey.isGroup() || accountKey.isRole() )
        {
            List<GroupEntity> groups =
                groupDao.findByUserStoreKeyAndGroupname( userStoreEntity.getKey(), accountKey.getLocalName(), false );
            if ( groups.size() > 0 )
            {
                return groups.get( 0 );
            }
        }
        else
        {
            UserEntity user = userDao.findByUserStoreKeyAndUsername( userStoreEntity.getKey(), accountKey.getLocalName() );
            return user.getUserGroup();
        }
        return null;
    }

    protected void updateUserstoreAdministrators( UserEntity user, UserStoreKey userStoreKey, AccountKeys administrators )
    {
        GroupSpecification spec = new GroupSpecification();
        spec.setType( GroupType.USERSTORE_ADMINS );
        spec.setDeletedState( GroupSpecification.DeletedState.NOT_DELETED );
        spec.setUserStoreKey( userStoreKey );
        List<GroupEntity> adminGroups = userStoreService.getGroups( spec );
        if ( adminGroups.size() == 1 )
        {
            GroupEntity adminGroup = adminGroups.get( 0 );
            UpdateGroupCommand command = new UpdateGroupCommand( user.getKey(), adminGroup.getGroupKey() );
            command.setName( adminGroup.getName() );
            for ( AccountKey administratorKey : administrators )
            {
                final GroupEntity groupMember = getGroupEntity( administratorKey );
                command.addMember( groupMember );
            }
            userStoreService.updateGroup( command );

            indexGroup( adminGroup.getGroupKey() );
        }

        spec.setType( GroupType.AUTHENTICATED_USERS );
        spec.setDeletedState( GroupSpecification.DeletedState.NOT_DELETED );
        adminGroups = userStoreService.getGroups( spec );
        if ( adminGroups.size() == 1 )
        {
            indexGroup( adminGroups.get( 0 ).getGroupKey() );
        }
    }

    protected void indexGroup( final GroupKey groupKey )
    {
        final GroupEntity groupEntity = securityService.getGroup( groupKey );
        if ( groupEntity == null )
        {
            searchService.deleteIndex( String.valueOf( groupKey ) );
            return;
        }

        final Group group = new Group();
        group.setKey( new com.enonic.wem.core.search.account.AccountKey( groupEntity.getGroupKey().toString() ) );
        group.setName( groupEntity.getName() );

        group.setDisplayName( groupEntity.getDescription() );

        group.setGroupType( groupEntity.getType() );
        if ( groupEntity.getUserStore() != null )
        {
            group.setUserStoreName( groupEntity.getUserStore().getName() );
        }

        group.setLastModified( null );

        final AccountIndexData accountIndexData = new AccountIndexDataEntity( group );

        searchService.index( accountIndexData );
    }

    protected UserStoreConfig getUserStoreConfig(
        final com.enonic.cms.core.security.userstore.config.UserStoreConfig userStoreEntityConfig )
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

    protected AccountKeys getUserStoreAdministrators( final UserStoreEntity userStoreEntity )
    {
        final List<AccountKey> adminAccounts = Lists.newArrayList();

        final GroupEntity builtInUserStoreAdministrator = groupDao.findBuiltInUserStoreAdministrator( userStoreEntity.getKey() );
        if ( builtInUserStoreAdministrator != null )
        {
            final Set<GroupEntity> userStoreAdmins = builtInUserStoreAdministrator.getMembers( false );

            final String userStoreName = userStoreEntity.getName();
            for ( GroupEntity groupEntity : userStoreAdmins )
            {
                if ( groupEntity.getUser() != null )
                {
                    adminAccounts.add( AccountKey.user( userStoreName + ":" + groupEntity.getUser().getName() ) );
                }
            }
        }
        return AccountKeys.from( adminAccounts );
    }

    @Autowired
    public void setUserDao( final UserDao userDao )
    {
        this.userDao = userDao;
    }

    @Autowired
    public void setUserStoreDao( final UserStoreDao userStoreDao )
    {
        this.userStoreDao = userStoreDao;
    }

    @Autowired
    public void setUserStoreService( final UserStoreService userStoreService )
    {
        this.userStoreService = userStoreService;
    }

    @Autowired
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Autowired
    public void setSearchService( final AccountSearchService searchService )
    {
        this.searchService = searchService;
    }

    @Autowired
    public void setGroupDao( final GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

    @Autowired
    public void setUserStoreConnectorManager( final UserStoreConnectorManager userStoreConnectorManager )
    {
        this.userStoreConnectorManager = userStoreConnectorManager;
    }
}
