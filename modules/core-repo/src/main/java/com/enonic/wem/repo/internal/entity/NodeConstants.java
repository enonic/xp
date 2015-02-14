package com.enonic.wem.repo.internal.entity;

import com.enonic.xp.core.security.PrincipalKey;
import com.enonic.xp.core.security.User;
import com.enonic.xp.core.security.UserStoreKey;
import com.enonic.xp.core.security.auth.AuthenticationInfo;

public final class NodeConstants
{
    public final static String binaryBlobStoreDir = "binary";

    public final static String nodeBlobStoreDir = "node";

    public static final PrincipalKey NODE_SUPER_USER_KEY = PrincipalKey.ofUser( UserStoreKey.system(), "node-su" );

    private static final User NODE_SUPER_USER = User.create().key( NODE_SUPER_USER_KEY ).login( "node" ).build();

    public static final AuthenticationInfo NODE_SU_AUTH_INFO = AuthenticationInfo.create().
        principals( NODE_SUPER_USER_KEY ).
        user( NODE_SUPER_USER ).
        build();
}
