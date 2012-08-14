package com.enonic.wem.core.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.account.ChangePassword;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.UserDao;

@Component
public final class ChangePasswordHandler
    extends CommandHandler<ChangePassword>
{
    private UserDao userDao;

    private SecurityService securityService;

    public ChangePasswordHandler()
    {
        super( ChangePassword.class );
    }

    @Override
    public void handle( final CommandContext context, final ChangePassword command )
        throws Exception
    {
        final AccountKey user = command.getKey();
        if ( !user.isUser() )
        {
            throw new AccountNotFoundException( user );
        }

        final QualifiedUsername userQualifiedName = QualifiedUsername.parse( user.getQualifiedName() );
        final UserEntity userEntity = userDao.findByQualifiedUsername( userQualifiedName );
        if ( userEntity == null )
        {
            throw new AccountNotFoundException( user );
        }

        securityService.changePassword( userQualifiedName, command.getPassword() );
        // TODO return false if password could not be changed, due to password policy for example.
        command.setResult( true );
    }

    @Autowired
    public void setUserDao( final UserDao userDao )
    {
        this.userDao = userDao;
    }

    @Autowired
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

}
