package com.enonic.wem.core.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.account.ValidatePassword;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

import com.enonic.cms.core.security.InvalidCredentialsException;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.UserDao;

@Component
public final class ValidatePasswordHandler
    extends CommandHandler<ValidatePassword>
{
    private UserDao userDao;

    private SecurityService securityService;

    public ValidatePasswordHandler()
    {
        super( ValidatePassword.class );
    }

    @Override
    public void handle( final CommandContext context, final ValidatePassword command )
        throws Exception
    {
        final AccountKey user = command.getKey();
        final QualifiedUsername userQualifiedName = QualifiedUsername.parse( user.getQualifiedName() );
        final UserEntity userEntity = userDao.findByQualifiedUsername( userQualifiedName );
        if ( userEntity == null )
        {
            throw new AccountNotFoundException( user );
        }

        try
        {
            securityService.loginPortalUser( userQualifiedName, command.getPassword() );
            command.setResult( true );
        }
        catch ( InvalidCredentialsException e )
        {
            command.setResult( false );
        }
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
