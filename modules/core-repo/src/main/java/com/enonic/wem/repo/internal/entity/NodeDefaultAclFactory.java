package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;

public class NodeDefaultAclFactory
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
