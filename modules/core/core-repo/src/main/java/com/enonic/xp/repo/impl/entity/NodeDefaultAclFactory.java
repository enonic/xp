package com.enonic.xp.repo.impl.entity;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

class NodeDefaultAclFactory
{
    public static AccessControlList create( final PrincipalKey creator )
    {
        return AccessControlList.create().
            add( AccessControlEntry.create().
                allowAll().
                principal( creator ).
                build() ).
            build();
    }
}
