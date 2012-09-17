package com.enonic.wem.core.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.NonUserAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.command.account.GetAccounts;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

import com.enonic.cms.core.security.QualifiedName;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

@Component
public final class GetAccountsHandler
    extends CommandHandler<GetAccounts>
{
    private UserStoreDao userStoreDao;

    private GroupDao groupDao;

    private UserDao userDao;

    private final UserProfileTransformer userProfileTransformer;

    public GetAccountsHandler()
    {
        super( GetAccounts.class );
        userProfileTransformer = new UserProfileTransformer();
    }

    @Override
    public void handle( final CommandContext context, final GetAccounts command )
        throws Exception
    {
        final boolean includeMembers = command.isIncludeMembers();
        final boolean includePhoto = command.isIncludeImage();
        final boolean includeProfile = command.isIncludeProfile();

        final Accounts accounts = fetchAccounts( command.getKeys(), includeMembers, includePhoto, includeProfile );

        command.setResult( accounts );
    }

    private Accounts fetchAccounts( final AccountKeys keys, final boolean includeMembers, final boolean includePhoto,
                                    final boolean includeProfile )
    {
        final List<Account> accountList = new ArrayList<Account>();
        for ( AccountKey key : keys )
        {
            switch ( key.getType() )
            {
                case USER:
                    final UserEntity user = findUserEntity( key );
                    if ( user != null )
                    {
                        accountList.add( buildUserAccount( user, includePhoto, includeProfile ) );
                    }
                    break;

                case GROUP:
                    final GroupEntity group = findGroupEntity( key );
                    if ( group != null )
                    {
                        accountList.add( buildGroupAccount( group, includeMembers ) );
                    }
                    break;

                case ROLE:
                    final GroupEntity role = findGroupEntity( key );
                    if ( role != null )
                    {
                        accountList.add( buildRoleAccount( role, includeMembers ) );
                    }
                    break;
            }
        }

        return Accounts.from( accountList );
    }

    private UserAccount buildUserAccount( final UserEntity user, final boolean includePhoto, final boolean includeProfile )
    {
        final UserAccount userAccount = UserAccount.create( qualifiedName( user.getQualifiedName() ) );
        userAccount.setDisplayName( user.getDisplayName() );
        userAccount.setEmail( user.getEmail() );
        userAccount.setLastLoginTime( DateTime.now() ); // TODO fix when login-time is stored in backend
        userAccount.setCreatedTime( user.getTimestamp() ); // TODO fix when created-time is stored in backend
        userAccount.setModifiedTime( user.getTimestamp() );
        userAccount.setDeleted( user.isDeleted() );
        userAccount.setEditable( true ); // TODO evaluate if account is editable in the current context
        if ( includePhoto )
        {
            userAccount.setImage( user.getPhoto() );
        }
        if ( includeProfile )
        {
            userAccount.setProfile( userProfileTransformer.userEntityToUserProfile( user ) );
        }
        return userAccount;
    }

    private GroupAccount buildGroupAccount( final GroupEntity groupEntity, final boolean includeMembers )
    {
        final GroupAccount group = GroupAccount.create( qualifiedName( groupEntity.getQualifiedName() ) );
        buildNonUserAccount( group, groupEntity, includeMembers );
        return group;
    }

    private RoleAccount buildRoleAccount( final GroupEntity groupEntity, final boolean includeMembers )
    {
        final RoleAccount role = RoleAccount.create( qualifiedName( groupEntity.getQualifiedName() ) );
        buildNonUserAccount( role, groupEntity, includeMembers );
        return role;
    }

    private void buildNonUserAccount( final NonUserAccount nonUser, final GroupEntity groupEntity, final boolean includeMembers )
    {
        nonUser.setDisplayName( groupEntity.getDescription() );
        final DateTime dummyTime = DateTime.parse( "2012-01-01T10:01:10.101+01:00" ); // temporary setting a fixed timestamp for testing
        nonUser.setCreatedTime( dummyTime ); // TODO fix when created-time is stored in backend
        nonUser.setModifiedTime( dummyTime ); // TODO fix when modified-time is stored in backend
        nonUser.setDeleted( groupEntity.isDeleted() );
        nonUser.setEditable( true ); // TODO evaluate if account is editable in the current context

        if ( includeMembers )
        {
            final AccountKeys accountMembers = buildAccountMembers( groupEntity );
            nonUser.setMembers( accountMembers );
        }
    }

    private AccountKeys buildAccountMembers( final GroupEntity groupEntity )
    {
        final Set<GroupEntity> members = groupEntity.getMembers( false );
        if ( ( members == null ) || members.isEmpty() )
        {
            return AccountKeys.empty();
        }

        final Set<AccountKey> keys = Sets.newHashSet();
        for ( GroupEntity member : members )
        {
            keys.add( memberToAccountKey( member ) );
        }
        return AccountKeys.from( keys );
    }

    private AccountKey memberToAccountKey( final GroupEntity groupEntity )
    {
        if ( groupEntity.getType() == GroupType.USER )
        {
            return AccountKey.user( qualifiedName( groupEntity.getUser().getQualifiedName() ) );
        }
        else if ( groupEntity.isBuiltIn() )
        {
            return AccountKey.role( qualifiedName( groupEntity.getQualifiedName() ) );
        }
        else
        {
            return AccountKey.group( qualifiedName( groupEntity.getQualifiedName() ) );
        }
    }

    private String qualifiedName( QualifiedName qualifiedName )
    {
        String qName = qualifiedName.toString().replace( '\\', ':' );
        if ( !qName.contains( ":" ) )
        {
            qName = "system:" + qName;
        }

        return qName;
    }

    private UserEntity findUserEntity( final AccountKey accountKey )
    {
        final QualifiedUsername qualifiedUserName = new QualifiedUsername( accountKey.getUserStore(), accountKey.getLocalName() );
        return userDao.findByQualifiedUsername( qualifiedUserName );
    }

    private GroupEntity findGroupEntity( final AccountKey accountKey )
    {
        final UserStoreKey userStoreKey = getUserStoreEntityKey( accountKey.getUserStore() );
        if ( userStoreKey == null )
        {
            if ( "system".equals( accountKey.getUserStore() ) )
            {
                return groupDao.findGlobalGroupByName( accountKey.getLocalName(), false );
            }
            return null;
        }
        final List<GroupEntity> memberAsGroup = groupDao.findByUserStoreKeyAndGroupname( userStoreKey, accountKey.getLocalName(), false );
        if ( memberAsGroup == null || memberAsGroup.isEmpty() )
        {
            return null;
        }
        return memberAsGroup.get( 0 );
    }

    private UserStoreKey getUserStoreEntityKey( final String userStoreName )
    {
        final UserStoreEntity userStoreEntity = userStoreDao.findByName( userStoreName );
        return userStoreEntity == null ? null : userStoreEntity.getKey();
    }

    @Autowired
    public void setGroupDao( final GroupDao groupDao )
    {
        this.groupDao = groupDao;
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

}
