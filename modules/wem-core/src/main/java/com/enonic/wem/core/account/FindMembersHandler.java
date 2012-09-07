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
import com.enonic.wem.api.command.account.FindMembers;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserStoreDao;

@Component
public final class FindMembersHandler
    extends CommandHandler<FindMembers>
{
    private GroupDao groupDao;

    private UserStoreDao userStoreDao;

    public FindMembersHandler()
    {
        super( FindMembers.class );
    }

    @Override
    public void handle( final CommandContext context, final FindMembers command )
        throws Exception
    {
        final AccountKey account = command.getKey();

        final String groupName = account.getLocalName();
        final UserStoreEntity userStore = userStoreDao.findByName( account.getUserStore() );
        final List<GroupEntity> groups = groupDao.findByUserStoreKeyAndGroupname( userStore.getKey(), groupName, false );
        if ( ( groups == null ) || groups.isEmpty() )
        {
            throw new AccountNotFoundException( account );
        }

        final GroupEntity group = groups.get( 0 );
        final Set<GroupEntity> groupMembers = group.getMembers( false );

        final Set<AccountKey> members = membersToAccountKeys( groupMembers, command.isIncludeTransitive() );

        final AccountKeys result = AccountKeys.from( members );
        command.setResult( result );
    }

    private Set<AccountKey> membersToAccountKeys( final Set<GroupEntity> members, boolean includeTransitive )
    {
        final Set<AccountKey> accountSet = Sets.newHashSet();
        for ( GroupEntity member : members )
        {
            final AccountType type = getAccountType( member );
            final String name = getAccountName( member );
            final AccountKey memberAccount = createAccountKey( type, member.getUserStore().getName(), name );
            accountSet.add( memberAccount );
            if ( includeTransitive )
            {
                final Set<AccountKey> transitiveAccountSet = membersToAccountKeys( member.getMembers( false ), includeTransitive );
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
}
