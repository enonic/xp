package com.enonic.wem.core.search.indexing;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.search.account.User;

@Component
public class UserLoader
    implements BatchLoader<User>
{
    @Autowired
    private AccountDao accountService;

    private int batchSize = 1;

    private int currentIndex = 0;

    private boolean hasNext = true;

    @Override
    public int getTotal()
    {
        return accountService.getUsersCount();
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
        final List<User> userList = accountService.findAllUsers( currentIndex, batchSize );
        currentIndex += userList.size();

        hasNext = !userList.isEmpty();

        return userList;
    }

    @Override
    public void reset()
    {
        currentIndex = 0;
        hasNext = true;
    }
}
