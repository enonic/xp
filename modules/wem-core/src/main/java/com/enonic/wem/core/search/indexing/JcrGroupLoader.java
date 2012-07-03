package com.enonic.wem.core.search.indexing;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.jcr.accounts.AccountJcrDao;
import com.enonic.wem.core.jcr.accounts.JcrGroup;
import com.enonic.wem.core.search.account.AccountKey;
import com.enonic.wem.core.search.account.Group;

import com.enonic.cms.core.security.group.GroupType;

//@Component
public class JcrGroupLoader
    implements BatchLoader<Group>
{
    @Autowired
    private AccountJcrDao accountDao;

    private int batchSize = 1;

    private int currentIndex = 0;

    private boolean hasNext = true;

    @Override
    public int getTotal()
    {
        return accountDao.getGroupsCount();
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
        final List<JcrGroup> jcrGroupList = accountDao.findAllGroups( currentIndex, batchSize );
        currentIndex += jcrGroupList.size();

        hasNext = !jcrGroupList.isEmpty();


        final List<Group> groupList = new ArrayList<Group>();
        for ( JcrGroup jcrGroup : jcrGroupList )
        {
            groupList.add( jcrUserToIndexUser( jcrGroup ) );
        }        return groupList;
    }

    @Override
    public void reset()
    {
        currentIndex = 0;
        hasNext = true;
    }

    private Group jcrUserToIndexUser(final JcrGroup jcrGroup) {
        Group group = new Group();
        group.setKey( new AccountKey( jcrGroup.getId() ) );
        group.setName( jcrGroup.getName() );
        group.setGroupType( GroupType.GLOBAL_GROUP);
        group.setDisplayName( jcrGroup.getDisplayName() );
        if ( jcrGroup.getUserStore() != null )
        {
            group.setUserStoreName( jcrGroup.getUserStore() );
        }
        group.setLastModified( jcrGroup.getLastModified() );
        return group;
    }
}
