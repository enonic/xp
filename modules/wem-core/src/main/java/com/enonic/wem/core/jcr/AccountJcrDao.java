package com.enonic.wem.core.jcr;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.store.support.EntityPageList;

public interface AccountJcrDao
{
    UserEntity findUserByKey( UserKey key );

    GroupEntity findGroupByKey( GroupKey key );

    EntityPageList<UserEntity> findAll( int index, int count, String query, String order );

    byte[] findUserPhotoByKey( String key );
}
