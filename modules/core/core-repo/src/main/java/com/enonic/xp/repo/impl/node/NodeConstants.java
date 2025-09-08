package com.enonic.xp.repo.impl.node;

import java.time.Clock;
import java.time.Duration;

import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class NodeConstants
{
    private static final PrincipalKey NODE_SUPER_USER_KEY = PrincipalKey.ofUser( IdProviderKey.system(), "node-su" );

    private static final User NODE_SUPER_USER = User.create().key( NODE_SUPER_USER_KEY ).login( "node" ).build();

    public static final String DEFAULT_FULLTEXT_SEARCH_ANALYZER = "fulltext_search_default";

    public static final String DEFAULT_NGRAM_SEARCH_ANALYZER = "ngram_search_default";

    public static final String DOCUMENT_INDEX_DEFAULT_ANALYZER = "document_index_default";

    public static final SegmentLevel NODE_SEGMENT_LEVEL = SegmentLevel.from( "node" );

    public static final SegmentLevel INDEX_CONFIG_SEGMENT_LEVEL = SegmentLevel.from( "index" );

    public static final SegmentLevel ACCESS_CONTROL_SEGMENT_LEVEL = SegmentLevel.from( "access" );

    public static final SegmentLevel BINARY_SEGMENT_LEVEL = SegmentLevel.from( "binary" );

    public static final AuthenticationInfo NODE_SU_AUTH_INFO = AuthenticationInfo.create().
        principals( NODE_SUPER_USER_KEY, RoleKeys.ADMIN ).
        user( NODE_SUPER_USER ).
        build();

    public static final Clock CLOCK = Clock.tick( Clock.systemUTC(), Duration.ofMillis( 1 ) );
}
