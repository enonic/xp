package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.Permission;

public class NodeDefaultAclFactory
{

    public static AccessControlList create( final PrincipalKey creator )
    {
        return AccessControlList.create().
            add( AccessControlEntry.create().
                allowAll().
                principal( creator ).
                build() ).
            // TODO: Temporary to get stuff up and running without setting ACL
                add( AccessControlEntry.create().
                allow( Permission.READ ).
                principal( User.anonymous().getKey() ).
                build() ).
            build();
    }
}
