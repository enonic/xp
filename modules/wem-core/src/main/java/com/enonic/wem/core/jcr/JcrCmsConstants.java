package com.enonic.wem.core.jcr;

public class JcrCmsConstants
{
    static final String ENONIC_CMS_NAMESPACE = "http://www.enonic.com/cms";

    static final String ENONIC_CMS_NAMESPACE_PREFIX = "cms";


    static final String ROOT_NODE = "enonic";

    static final String USERSTORES_NODE = "userstores";

    static final String USERSTORES_NODE_TYPE = "cms:userstores";

    static final String USERSTORE_NODE_TYPE = "cms:userstore";

    static final String GROUPS_NODE = "groups";

    static final String GROUPS_NODE_TYPE = "cms:groups";

    static final String GROUP_NODE_TYPE = "cms:group";

    static final String USERS_NODE = "users";

    static final String USERS_NODE_TYPE = "cms:users";

    static final String USER_NODE_TYPE = "cms:user";

    static final String ROLES_NODE = "roles";

    static final String ROLES_NODE_TYPE = "cms:roles";

    static final String SYSTEM_USERSTORE_NODE = "system";

    static final int SYSTEM_USERSTORE_KEY = 0;

    static final String USERSTORES_PATH = JcrCmsConstants.ROOT_NODE + "/" + JcrCmsConstants.USERSTORES_NODE + "/";

    static final String USERSTORES_ABSOLUTE_PATH = "/" + USERSTORES_PATH;


}
