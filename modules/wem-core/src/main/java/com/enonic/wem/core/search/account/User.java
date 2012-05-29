package com.enonic.wem.core.search.account;

import com.enonic.cms.api.client.model.user.UserInfo;

public class User extends Account
{
    public User()
    {
        super(AccountType.USER);
    }

    private String email;

    private UserInfo userInfo;

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public UserInfo getUserInfo()
    {
        return userInfo;
    }

    public void setUserInfo( UserInfo userInfo )
    {
        this.userInfo = userInfo;
    }
}
