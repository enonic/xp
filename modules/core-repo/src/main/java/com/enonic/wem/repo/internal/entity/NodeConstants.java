package com.enonic.wem.repo.internal.entity;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class NodeConstants
{
    public final static String BINARY_BLOB_STORE_DIR = "binary";

    public final static String NODE_BLOB_STORE_DIR = "node";

    public static final PrincipalKey NODE_SUPER_USER_KEY = PrincipalKey.ofUser( UserStoreKey.system(), "node-su" );

    private static final User NODE_SUPER_USER = User.create().key( NODE_SUPER_USER_KEY ).login( "node" ).build();

    public static final String DEFAULT_FULLTEXT_SEARCH_ANALYZER = "fulltext_default";

    public static final String DEFAULT_NGRAM_SEARCH_ANALYZER = "ngram_search";

    public static final AuthenticationInfo NODE_SU_AUTH_INFO = AuthenticationInfo.create().
        principals( NODE_SUPER_USER_KEY, RoleKeys.ADMIN ).
        user( NODE_SUPER_USER ).
        build();
}
