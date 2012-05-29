package com.enonic.wem.core.search.indexing;

import java.util.List;
import java.util.ListIterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.search.account.Group;
import com.enonic.cms.core.security.group.GroupType;

@Component
public class GroupLoader
    implements BatchLoader<Group>
{
    @Autowired
    private AccountDao accountService;

    private int batchSize = 1;

    private int currentIndex = 0;

    private boolean hasNext = true;

    @Override
    public int getTotal()
    {
        return accountService.getGroupsCount();
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
    public List<Group> next()
    {
        final List<Group> groupList = accountService.findAllGroups( currentIndex, batchSize );
        currentIndex += groupList.size();

        hasNext = !groupList.isEmpty();

        filterGroups(groupList);
        return groupList;
    }

    private void filterGroups( final List<Group> groupList )
    {
        ListIterator<Group> groupIterator = groupList.listIterator();
        while ( groupIterator.hasNext() )
        {
            Group group = groupIterator.next();
            if ( group.getGroupType() == GroupType.USER )
            {
                groupIterator.remove();
            }
        }
    }

    @Override
    public void reset()
    {
        currentIndex = 0;
        hasNext = true;
    }
}
