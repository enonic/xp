package com.enonic.wem.api.support;


import org.joda.time.DateTime;

import com.enonic.wem.api.account.UserKey;

public interface ChangeTraceable
{
    public DateTime getCreatedTime();

    public DateTime getModifiedTime();

    public UserKey getCreator();

    public UserKey getModifier();

}
