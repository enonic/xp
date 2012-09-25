package com.enonic.wem.core.search.indexing;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.wem.core.search.UserInfoHelper;
import com.enonic.wem.core.search.account.AccountKey;
import com.enonic.wem.core.search.account.Group;
import com.enonic.wem.core.search.account.User;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.GroupEntityDao;
import com.enonic.cms.store.dao.UserEntityDao;
import com.enonic.cms.store.support.EntityPageList;

@Component
class AccountDaoOldImpl
    implements InitializingBean, AccountDao
{
    private HibernateTemplate hibernateTemplate;

    private GroupEntityDao groupDao;

    private UserEntityDao userDao;

    public void afterPropertiesSet()
        throws Exception
    {
        this.userDao = new UserEntityDao();
        this.userDao.setHibernateTemplate( this.hibernateTemplate );
        this.groupDao = new GroupEntityDao();
        this.groupDao.setHibernateTemplate( this.hibernateTemplate );
    }

    @Override
    public int getGroupsCount()
    {
        return this.groupDao.findAll( false ).size();
    }

    @Override
    public int getUsersCount()
    {
        return this.userDao.findAll( false ).size();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<User> findAllUsers( int from, int count )
    {
        EntityPageList<UserEntity> users = userDao.findAll( from, count );
        List<User> list = new ArrayList<User>();
        for ( UserEntity user : users.getList() )
        {
            list.add( convertUserEntityToAccount( user ) );
        }
        return list;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<Group> findAllGroups( int from, int count )
    {
        EntityPageList<GroupEntity> groups = groupDao.findAll( from, count );

        List<Group> list = new ArrayList<Group>();
        for ( GroupEntity group : groups.getList() )
        {
            if ( !group.isAnonymous() )
            {
                list.add( convertGroupEntityToAccount( group ) );
            }
        }
        return list;
    }

    private Group convertGroupEntityToAccount( GroupEntity groupEntity )
    {
        final Group group = new Group(groupEntity.isBuiltIn());
        group.setKey( new AccountKey( groupEntity.getGroupKey().toString() ) );
        group.setName( groupEntity.getName() );

        // TODO: Group have no display name - use description for now
        // group.setDisplayName( groupEntity.getDisplayName() );
        group.setDisplayName( groupEntity.getDescription() );

        group.setGroupType( groupEntity.getType() );
        if ( groupEntity.getUserStore() != null )
        {
            group.setUserStoreName( groupEntity.getUserStore().getName() );
        }

        // TODO: Group have no last-modified - use "null" for now
        // final DateTime lastModified = ( groupEntity.getLastModified() == null ) ? null : new DateTime( groupEntity.getLastModified() );
        // group.setLastModified( lastModified );
        group.setLastModified( null );

        return group;
    }

    private User convertUserEntityToAccount( UserEntity userEntity )
    {
        final User user = new User();
        user.setKey( new AccountKey( userEntity.getKey().toString() ) );
        user.setName( userEntity.getName() );
        user.setEmail( userEntity.getEmail() );
        user.setDisplayName( userEntity.getDisplayName() );
        if ( userEntity.getUserStore() != null )
        {
            user.setUserStoreName( userEntity.getUserStore().getName() );
        }
        user.setLastModified( userEntity.getTimestamp() );
        user.setUserInfo( UserInfoHelper.toUserInfo( userEntity ) );

        return user;
    }

    @Autowired
    public void setHibernateTemplate( HibernateTemplate hibernateTemplate )
    {
        this.hibernateTemplate = hibernateTemplate;
    }

}
