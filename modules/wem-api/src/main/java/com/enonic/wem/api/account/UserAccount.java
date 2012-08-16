package com.enonic.wem.api.account;

import org.joda.time.DateTime;

public interface UserAccount
    extends Account
{
    public String getEmail();

    public byte[] getPhoto();

    public DateTime getLastLoginTime();
}
