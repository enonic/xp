package com.enonic.wem.core.account;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.api.command.account.FindMemberships;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

@Component
public final class FindMembershipsHandler
    extends CommandHandler<FindMemberships>
{
    private GroupDao groupDao;

    private UserDao userDao;

    private UserStoreDao userStoreDao;

    public FindMembershipsHandler()
    {
        super( FindMemberships.class );
    }

    @Override
    public void handle( final CommandContext context, final FindMemberships command )
        throws Exception
    {
        final AccountKey account = command.getKey();

        final String accountName = account.getLocalName();
        final UserStoreEntity userStore = userStoreDao.findByName( account.getUserStore() );
        if ( ( userStore == null ) && ( !"system".equals( account.getUserStore() ) ) )
        {
            throw new AccountNotFoundException( account );
        }

        final GroupEntity groupEntity;
        if ( account.isUser() )
        {
            groupEntity = findUserGroup( userStore, accountName );
        }
        else
        {
            groupEntity = findGroupOrRole( userStore, accountName );
        }

        if ( groupEntity == null )
        {
            throw new AccountNotFoundException( account );
        }

        final Set<GroupEntity> groupMemberships = groupEntity.getMemberships( false );

        final Set<AccountKey> members = membershipsToAccountKeys( groupMemberships, command.isIncludeTransitive() );

        command.setResult( AccountKeys.from( members ) );
    }

    private GroupEntity findUserGroup( final UserStoreEntity userStore, final String accountName )
    {
        if ( userStore == null )
        {
            final UserEntity globalUser = userDao.findBuiltInGlobalByName( accountName );
            return globalUser == null ? null : globalUser.getUserGroup();
        }

        final UserEntity user = userDao.findByQualifiedUsername( new QualifiedUsername( userStore.getName(), accountName ) );
        if ( user == null )
        {
            return null;
        }
        return user.getUserGroup();
    }

    private GroupEntity findGroupOrRole( final UserStoreEntity userStore, final String groupName )
    {
        if ( userStore == null )
        {
            return this.groupDao.findGlobalGroupByName( groupName, false );
        }

        final List<GroupEntity> groups = groupDao.findByUserStoreKeyAndGroupname( userStore.getKey(), groupName, false );
        if ( ( groups == null ) || groups.isEmpty() )
        {
            return null;
        }
        return groups.get( 0 );
    }

    private Set<AccountKey> membershipsToAccountKeys( final Set<GroupEntity> members, boolean includeTransitive )
    {
        final Set<AccountKey> accountSet = Sets.newHashSet();
        for ( GroupEntity member : members )
        {
            final AccountType type = getAccountType( member );
            final String name = getAccountName( member );
            final String userStoreName = member.getUserStore() == null ? "system" : member.getUserStore().getName();
            final AccountKey memberAccount = createAccountKey( type, userStoreName, name );
            accountSet.add( memberAccount );

            if ( includeTransitive )
            {
                final Set<AccountKey> transitiveAccountSet = membershipsToAccountKeys( member.getMemberships( false ), includeTransitive );
                accountSet.addAll( transitiveAccountSet );
            }
        }
        return accountSet;
    }

    private String getAccountName( final GroupEntity group )
    {
        if ( group.getType() == GroupType.USER )
        {
            return group.getUser().getName();
        }
        else
        {
            return group.getName();
        }
    }

    private AccountKey createAccountKey( final AccountType type, final String userStore, final String localName )
    {
        return AccountKey.from( Joiner.on( ":" ).join( type.toString().toLowerCase(), userStore, localName ) );
    }

    private AccountType getAccountType( final GroupEntity groupEntity )
    {
        final GroupType type = groupEntity.getType();
        if ( type == GroupType.USER )
        {
            return AccountType.USER;
        }
        else if ( groupEntity.isBuiltIn() )
        {
            return AccountType.ROLE;
        }
        else
        {
            return AccountType.GROUP;
        }
    }

    @Autowired
    public void setGroupDao( final GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

    @Autowired
    public void setUserStoreDao( final UserStoreDao userStoreDao )
    {
        this.userStoreDao = userStoreDao;
    }

    @Autowired
    public void setUserDao( final UserDao userDao )
    {
        this.userDao = userDao;
    }
}
