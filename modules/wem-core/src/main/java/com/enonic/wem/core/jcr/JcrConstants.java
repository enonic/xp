package com.enonic.wem.core.jcr;

public interface JcrConstants
    extends org.apache.jackrabbit.JcrConstants
{
    public static final String WEM_NS = "http://www.enonic.com/wem";

    public static final String WEM_NS_PREFIX = "wem";

    public static final String ROOT_NODE = "wem";

    public static final String USER_STORES_NODE = "userStores";

    public static final String USER_STORES_TYPE = "wem:userStores";

    public static final String USER_STORE_TYPE = "wem:userStore";

    public static final String USER_STORE_ADMINISTRATORS_PROPERTY = "administrators";

    public static final String GROUPS_NODE = "groups";

    public static final String GROUPS_TYPE = "wem:groups";

    public static final String GROUP_TYPE = "wem:group";

    public static final String MEMBERS_NODE = "members";

    public static final String MEMBERS_PROPERTY = "members";

    public static final String MEMBER_NODE = "member";

    public static final String USERS_NODE = "users";

    public static final String USERS_TYPE = "wem:users";

    public static final String USER_TYPE = "wem:user";

    public static final String USER_PROFILE_TYPE = "wem:profile";

    public static final String USER_PROFILE_NODE = "profile";

    public static final String ROLES_NODE = "roles";

    public static final String ROLES_TYPE = "wem:roles";

    public static final String ROLE_TYPE = "wem:role";

    public static final String ACCOUNT_TYPE = "wem:account";

    public static final String USER_STORES_PATH = ROOT_NODE + "/" + USER_STORES_NODE + "/";

    public static final String USER_STORES_ABSOLUTE_PATH = "/" + USER_STORES_PATH;
}
