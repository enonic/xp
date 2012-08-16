package com.enonic.wem.api.account;

import org.joda.time.DateTime;

public interface Account
{
    public AccountKey getKey();

    public String getDisplayName();

    public DateTime getCreatedTime();

    public DateTime getModifiedTime();

    public boolean isDeleted();

    public boolean isEditable();
}
