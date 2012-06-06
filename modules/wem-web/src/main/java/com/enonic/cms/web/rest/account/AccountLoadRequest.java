package com.enonic.cms.web.rest.account;

import java.util.Arrays;
import java.util.List;

import com.enonic.cms.web.rest.common.LoadStoreRequest;

public class AccountLoadRequest
    extends LoadStoreRequest
{


    static private String SELECT_USERS = "users";

    static private String SELECT_GROUPS = "groups";

    static private String SELECT_ROLES = "roles";

    static private List<String> DEFAULT_TYPES =
            Arrays.asList(new String[]{SELECT_ROLES, SELECT_USERS, SELECT_GROUPS});

    private String userstores = "";

    private List<String> types = DEFAULT_TYPES;

    private String currentGroupKey = "";

    private String organizations = "";


    public void setType(List<String> types)
    {
        if (types.size() > 0)
        {
            this.types = types;
        }
        else
        {
            this.types = DEFAULT_TYPES;
        }
    }

    public List<String> getTypes()
    {
        return types;
    }

    public String getUserstores()
    {
        return userstores;
    }

    public void setUserstores( String userstores )
    {
        this.userstores = userstores;
    }

    public boolean isSelectUsers()
    {
        return types.contains( SELECT_USERS );
    }

    public boolean isSelectGroups()
    {
        return types.contains( SELECT_GROUPS );
    }

    public boolean isSelectRoles()
    {
        return types.contains( SELECT_ROLES );
    }

    public String getOrganizations()
    {
        return organizations;
    }

    public void setOrganizations( String organizations )
    {
        this.organizations = organizations;
    }

    public String getCurrentGroupKey()
    {
        return currentGroupKey;
    }

    public void setCurrentGroupKey( String currentGroupKey )
    {
        this.currentGroupKey = currentGroupKey;
    }

}
