package com.enonic.wem.web.rest.service.account;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.search.UserInfoHelper;
import com.enonic.wem.core.search.account.AccountIndexData;
import com.enonic.wem.core.search.account.AccountKey;
import com.enonic.wem.core.search.account.AccountSearchService;
import com.enonic.wem.web.rest2.resource.old.AccountModelTranslator;
import com.enonic.wem.web.rest2.resource.old.UserModel;
import com.enonic.wem.web.rest2.resource.account.user.UserUpdateResult;

import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.UserDao;

@Component
public class UserUpdateService
{

    @Autowired
    private AccountModelTranslator accountModelTranslator;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AccountSearchService searchService;

    public UserUpdateResult updateUser( String key, UserModel user )
    {
        final boolean isValid = isValidUserData( user );
        if ( isValid )
        {
            // TODO: here should be full path to image file. Probably UploadResource can provide it.
            final String photoRef = user.getPhoto();
            if ( StringUtils.isNotEmpty( photoRef ) )
            {
                final File photoFile = new File( photoRef );
                if ( photoFile.exists() )
                {
                    user.setPhoto( photoFile.getAbsolutePath() );
                }
                else
                {
                    user.setPhoto( null );
                }
            }

            if ( user.getKey() == null )
            {
                StoreNewUserCommand command = accountModelTranslator.getUserModelTranslator().toNewUserCommand( user );
                UserKey userKey = userStoreService.storeNewUser( command );
                indexUser( userKey.toString() );
                return new UserUpdateResult( userKey.toString() );
            }
            else
            {
                UpdateUserCommand command = accountModelTranslator.getUserModelTranslator().toUpdateUserCommand( user );
                userStoreService.updateUser( command );
                indexUser( user.getKey() );
                return new UserUpdateResult( user.getKey().toString() );
            }
        }
        else
        {
            return new UserUpdateResult( false, "Validation failed" );
        }
    }

    private void indexUser( String userKey )
    {
        final UserEntity userEntity = userDao.findByKey( userKey );
        if ( userEntity == null )
        {
            searchService.deleteIndex( userKey );
            return;
        }

        final com.enonic.wem.core.search.account.User user = new com.enonic.wem.core.search.account.User();
        user.setKey( new AccountKey( userEntity.getKey().toString() ) );
        user.setName( userEntity.getName() );
        user.setEmail( userEntity.getEmail() );
        user.setDisplayName( userEntity.getDisplayName() );
        user.setUserStoreName( userEntity.getUserStore().getName() );
        user.setLastModified( userEntity.getTimestamp() );
        user.setUserInfo( UserInfoHelper.toUserInfo( userEntity ) );
        final AccountIndexData accountIndexData = new AccountIndexData( user );
        searchService.index( accountIndexData );
    }

    private boolean isValidUserData( UserModel userData )
    {
        boolean isValid = StringUtils.isNotBlank( userData.getDisplayName() ) && StringUtils.isNotBlank( userData.getName() ) &&
            StringUtils.isNotBlank( userData.getEmail() );
        return isValid;
    }
}
