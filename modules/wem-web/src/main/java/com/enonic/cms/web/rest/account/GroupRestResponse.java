package com.enonic.cms.web.rest.account;

import com.enonic.cms.web.rest.common.RestResponse;

public class GroupRestResponse extends RestResponse
{
    private String groupkey;

    private AccountModel group;

    public String getGroupkey()
    {
        return groupkey;
    }

    public void setGroupkey( String groupkey )
    {
        this.groupkey = groupkey;
    }

    public AccountModel getGroup()
    {
        return group;
    }

    public void setGroup( AccountModel group )
    {
        this.group = group;
    }
}
