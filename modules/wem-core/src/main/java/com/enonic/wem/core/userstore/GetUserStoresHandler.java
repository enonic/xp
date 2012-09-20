package com.enonic.wem.core.userstore;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.wem.api.command.userstore.GetUserStores;
import com.enonic.wem.api.exception.UserStoreNotFoundException;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.UserStores;
import com.enonic.wem.api.userstore.statistics.UserStoreStatistics;
import com.enonic.wem.core.command.CommandContext;

import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;

@Component
public class GetUserStoresHandler
    extends UserStoreHandler<GetUserStores>
{

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

}
