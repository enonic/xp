package com.enonic.wem.core.search.indexing;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.wem.core.jcr.old.accounts.AccountJcrDao;
import com.enonic.wem.core.jcr.old.accounts.JcrUser;
import com.enonic.wem.core.search.account.AccountKey;
import com.enonic.wem.core.search.account.User;

import com.enonic.cms.api.client.model.user.UserInfo;

//@Component
public class JcrUserLoader
    implements BatchLoader<User>
{
    @Autowired
    private AccountJcrDao accountDao;

    private int batchSize = 1;

    private int currentIndex = 0;

    private boolean hasNext = true;

    @Override
    public int getTotal()
    {
        return accountDao.getUsersCount();
    }

    @Override
    public void setBatchSize( int size )
    {
        batchSize = size;
    }

    @Override
    public boolean hasNext()
    {
        return hasNext;
    }

    @Override
    public List<User> next()
    {
        final List<JcrUser> jcrUserList = accountDao.findAllUsers( currentIndex, batchSize );
        currentIndex += jcrUserList.size();

        hasNext = !jcrUserList.isEmpty();

        final List<User> userList = new ArrayList<User>();
        for ( JcrUser jcrUser : jcrUserList )
        {
            userList.add( jcrUserToIndexUser( jcrUser ) );
        }
        return userList;
    }

    @Override
    public void reset()
    {
        currentIndex = 0;
        hasNext = true;
    }

    private User jcrUserToIndexUser(final JcrUser jcrUser) {
        User user = new User();
        user.setKey( new AccountKey( jcrUser.getId() ) );
        user.setName( jcrUser.getName() );
        user.setEmail( jcrUser.getEmail() );
        user.setDisplayName( jcrUser.getDisplayName() );
        if ( jcrUser.getUserStore() != null )
        {
            user.setUserStoreName( jcrUser.getUserStore() );
        }
        user.setLastModified( jcrUser.getLastModified() );
        user.setUserInfo( new UserInfo() );
        return user;
    }
}
