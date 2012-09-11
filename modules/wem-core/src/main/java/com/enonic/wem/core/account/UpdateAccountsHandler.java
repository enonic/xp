package com.enonic.wem.core.account;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.NonUserAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.editor.AccountEditor;
import com.enonic.wem.api.command.account.UpdateAccounts;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

import com.enonic.cms.core.security.QualifiedName;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.group.UpdateGroupCommand;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

@Component
public final class UpdateAccountsHandler
    extends CommandHandler<UpdateAccounts>
{
    private UserStoreDao userStoreDao;

    private SecurityService securityService;

    private UserStoreService userStoreService;

    private GroupDao groupDao;

    private UserDao userDao;

    public UpdateAccountsHandler()
    {
        super( UpdateAccounts.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateAccounts command )
        throws Exception
    {
        final AccountKeys accountKeys = command.getKeys();
        final AccountEditor editor = command.getEditor();

        int accountsUpdated = 0;
        for ( AccountKey accountKey : accountKeys )
        {
            final Account account = retrieveAccount( accountKey );
            if ( account != null )
            {
                final boolean flag = editor.edit( account );
                if ( flag )
                {
                    updateAccount( account );
                    accountsUpdated++;
                }
            }
        }

        command.setResult( accountsUpdated );
    }

    private Account retrieveAccount( final AccountKey account )
    {
        return account.isUser() ? retrieveUser( account ) : retrieveGroup( account );
    }

    private UserAccount retrieveUser( final AccountKey userAccount )
    {
        final UserEntity userEntity = findUserEntity( userAccount );
        if ( userEntity == null )
        {
            return null;
        }

        return buildUserAccount( userEntity );
    }

    private NonUserAccount retrieveGroup( final AccountKey groupAccount )
    {
        final GroupEntity groupEntity = findGroupEntity( groupAccount );
        if ( groupEntity == null )
        {
            return null;
        }

        if ( groupEntity.isBuiltIn() )
        {
            return buildRoleAccount( groupEntity );
        }
        else
        {
            return buildGroupAccount( groupEntity );
        }
    }

    private UserAccount buildUserAccount( final UserEntity userEntity )
    {
        final UserAccount user = UserAccount.create( qualifiedName( userEntity.getQualifiedName() ) );
        user.setDisplayName( userEntity.getDisplayName() );
        user.setEmail( userEntity.getEmail() );
        user.setLastLoginTime( DateTime.now() ); // TODO fix when login-time is stored in backend
        user.setCreatedTime( userEntity.getTimestamp() ); // TODO fix when created-time is stored in backend
        user.setImage( userEntity.getPhoto() );
        user.setModifiedTime( userEntity.getTimestamp() );
        user.setDeleted( userEntity.isDeleted() );
        user.setEditable( true ); // TODO evaluate if account is editable in the current context
        return user;
    }

    private GroupAccount buildGroupAccount( final GroupEntity groupEntity )
    {
        final GroupAccount group = GroupAccount.create( qualifiedName( groupEntity.getQualifiedName() ) );
        buildNonUserAccount( group, groupEntity );
        return group;
    }

    private RoleAccount buildRoleAccount( final GroupEntity groupEntity )
    {
        final RoleAccount role = RoleAccount.create( qualifiedName( groupEntity.getQualifiedName() ) );
        buildNonUserAccount( role, groupEntity );
        return role;
    }

    private void buildNonUserAccount( final NonUserAccount nonUser, final GroupEntity groupEntity )
    {
        nonUser.setDisplayName( groupEntity.getDescription() );
        nonUser.setCreatedTime( DateTime.now() ); // TODO fix when created-time is stored in backend
        nonUser.setModifiedTime( DateTime.now() ); // TODO fix when modified-time is stored in backend
        nonUser.setDeleted( groupEntity.isDeleted() );
        nonUser.setEditable( true ); // TODO evaluate if account is editable in the current context

        final AccountKeys accountMembers = buildAccountMembers( groupEntity );
        nonUser.setMembers( accountMembers );
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

    private void updateAccount( final Account account )
    {
        if ( account.getKey().isUser() )
        {
            updateUser( (UserAccount) account );
        }
        else
        {
            updateGroupOrRole( (NonUserAccount) account );
        }
    }

    private void updateUser( UserAccount user )
    {
        final UserSpecification specification = new UserSpecification();
        final UserStoreEntity userStore = getUserStoreEntity( user.getKey().getUserStore() );
        specification.setUserStoreKey( userStore.getKey() );
        specification.setName( user.getKey().getLocalName() );
        specification.setDeletedStateNotDeleted();

//        final UserEntity updater = securityService.getImpersonatedPortalUser();
        final UserEntity updater = userDao.findBuiltInEnterpriseAdminUser(); // TODO get logged in user
        final UpdateUserCommand command = new UpdateUserCommand( updater.getKey(), specification );
        command.setupModifyStrategy();
        command.setAllowUpdateSelf( true );
        command.setEmail( user.getEmail() );
        command.setDisplayName( user.getDisplayName() );

        userStoreService.updateUser( command );
    }

    private void updateGroupOrRole( final NonUserAccount groupOrRole )
    {
//        final User oldUser = securityService.getLoggedInAdminConsoleUser();
        final UserEntity oldUser = userDao.findBuiltInEnterpriseAdminUser(); // TODO get logged in user
        final UserEntity user = securityService.getUser( oldUser );

        final GroupKey groupKey = findGroupEntity( groupOrRole.getKey() ).getGroupKey();
        final String name = groupOrRole.getKey().getLocalName();
        final String description = groupOrRole.getDisplayName();

        final Set<AccountKey> members = groupOrRole.getMembers() == null ? null : groupOrRole.getMembers().getSet();

        final UpdateGroupCommand command = new UpdateGroupCommand( user.getKey(), groupKey );
        command.setName( name );
        command.setRestricted( false );
        command.setDescription( description );
        if ( members != null )
        {
            for ( AccountKey member : members )
            {
                if ( member.isUser() )
                {
                    UserEntity memberAsUser = findUserEntity( member );
                    if ( memberAsUser != null )
                    {
                        GroupEntity memberAsUserUserGroup = memberAsUser.getUserGroup();
                        if ( memberAsUserUserGroup != null )
                        {
                            command.addMember( memberAsUserUserGroup );
                        }
                    }
                }
                else
                {
                    GroupEntity memberAsGroup = findGroupEntity( member );
                    if ( memberAsGroup != null )
                    {
                        command.addMember( memberAsGroup );
                    }
                }
            }
            command.syncMembers();
        }
        else
        {
            command.syncMembers();
        }

        userStoreService.updateGroup( command );
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

    private UserStoreEntity getUserStoreEntity( final String userStoreName )
    {
        return userStoreDao.findByName( userStoreName );
    }

    private UserStoreKey getUserStoreEntityKey( final String userStoreName )
    {
        final UserStoreEntity userStoreEntity = getUserStoreEntity( userStoreName );
        return userStoreEntity == null ? null : userStoreEntity.getKey();
    }

    private String qualifiedName( QualifiedName qualifiedName )
    {
        return qualifiedName.toString().replace( '\\', ':' );
    }

    @Autowired
    public void setUserStoreDao( final UserStoreDao userStoreDao )
    {
        this.userStoreDao = userStoreDao;
    }

    @Autowired
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
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

    @Autowired
    public void setUserDao( final UserDao userDao )
    {
        this.userDao = userDao;
    }
}
