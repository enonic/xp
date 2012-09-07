package com.enonic.wem.core.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.command.account.CreateAccount;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.group.StoreNewGroupCommand;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreParser;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.user.field.UserFields;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

@Component
public final class CreateAccountHandler
    extends CommandHandler<CreateAccount>
{
    private UserStoreDao userStoreDao;

    private SecurityService securityService;

    private UserStoreService userStoreService;

    private GroupDao groupDao;

    private UserDao userDao;

    public CreateAccountHandler()
    {
        super( CreateAccount.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateAccount command )
        throws Exception
    {
        final Account account = command.getAccount();
        final AccountKey key = account.getKey();
        if ( key.isUser() )
        {
            createUser( (UserAccount) account );
        }
        else if ( key.isGroup() )
        {

            createGroup( (GroupAccount) account );
        }

        command.setResult( key );
    }

    private void createUser( final UserAccount user )
    {
//        final UserEntity storer = securityService.getImpersonatedPortalUser();
        final UserEntity storer = userDao.findBuiltInEnterpriseAdminUser(); // TODO get logged in user
        final UserStoreEntity userStoreEntity = userStoreDao.findByName( user.getKey().getUserStore() );
        if ( userStoreEntity == null )
        {
            throw new SystemException( "Userstore [{0}] not found", user.getKey().getUserStore() );
        }
        final String userStoreKey = userStoreEntity.getKey().toString();
        final UserStoreEntity userStore = new UserStoreParser( userStoreDao ).parseUserStore( userStoreKey );

        final StoreNewUserCommand storeNewUserCommand = new StoreNewUserCommand();
        storeNewUserCommand.setUsername( user.getKey().getLocalName() );
        storeNewUserCommand.setEmail( user.getEmail() );
        final UserFields userFields = new UserFields();

        if ( user.getDisplayName() == null )
        {
            storeNewUserCommand.setDisplayName( user.getKey().getLocalName() );
        }
        else
        {
            storeNewUserCommand.setDisplayName( user.getDisplayName() );
        }
        storeNewUserCommand.setPassword( "" );
        storeNewUserCommand.setUserFields( userFields );

        storeNewUserCommand.setType( UserType.NORMAL );
        storeNewUserCommand.setUserStoreKey( userStore.getKey() );
        storeNewUserCommand.setStorer( storer.getKey() );
        storeNewUserCommand.setAllowAnyUserAccess( false );

        userStoreService.storeNewUser( storeNewUserCommand );
    }

    private void createGroup( final GroupAccount group )
    {
        final UserStoreEntity userStoreEntity = userStoreDao.findByName( group.getKey().getUserStore() );
        if ( userStoreEntity == null )
        {
            throw new SystemException( "Userstore [{0}] not found", group.getKey().getUserStore() );
        }
        final String userStoreKey = userStoreEntity.getKey().toString();
        final UserStoreEntity userStore = new UserStoreParser( userStoreDao ).parseUserStore( userStoreKey );

//        final UserEntity runningUser = securityService.getImpersonatedPortalUser();
        final UserEntity runningUser = userDao.findBuiltInEnterpriseAdminUser(); // TODO get logged in user

        StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setName( group.getKey().getLocalName() );
        storeNewGroupCommand.setRestriced( true );
        storeNewGroupCommand.setExecutor( runningUser );
        storeNewGroupCommand.setDescription( group.getDisplayName() );
        storeNewGroupCommand.setUserStoreKey( userStore.getKey() );
        storeNewGroupCommand.setType( GroupType.USERSTORE_GROUP );
        storeNewGroupCommand.setRespondWithException( false );

        final AccountKeys members = group.getMembers();
        if ( members != null )
        {
            for ( AccountKey member : members )
            {
                final GroupKey groupKey = memberAccountToGroupEntity( member );
                if ( groupKey != null )
                {
                    storeNewGroupCommand.addMember( groupKey );
                }
            }
        }

        userStoreService.storeNewGroup( storeNewGroupCommand );
    }

    private GroupKey memberAccountToGroupEntity( final AccountKey accountKey )
    {
        if ( accountKey.isUser() )
        {
            return userAccountToGroupEntity( accountKey );
        }
        else
        {
            return groupAccountToGroupEntity( accountKey );
        }
    }

    private GroupKey userAccountToGroupEntity( final AccountKey accountKey )
    {
        final UserStoreEntity userStoreEntity = userStoreDao.findByName( accountKey.getUserStore() );
        if ( userStoreEntity == null )
        {
            return null;
        }

        final QualifiedUsername qualifiedUsername = new QualifiedUsername( userStoreEntity.getName(), accountKey.getLocalName() );
        final UserEntity user = userDao.findByQualifiedUsername( qualifiedUsername );
        return user == null ? null : user.getUserGroup().getGroupKey();
    }

    private GroupKey groupAccountToGroupEntity( final AccountKey accountKey )
    {
        final UserStoreEntity userStoreEntity = userStoreDao.findByName( accountKey.getUserStore() );
        if ( userStoreEntity == null )
        {
            return null;
        }

        final UserStoreKey userStoreKey = userStoreEntity.getKey();
        final List<GroupEntity> group = groupDao.findByUserStoreKeyAndGroupname( userStoreKey, accountKey.getLocalName(), false );
        return group.isEmpty() ? null : group.get( 0 ).getGroupKey();
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
