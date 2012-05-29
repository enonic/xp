package com.enonic.wem.core.search.account;

import com.enonic.cms.core.security.group.GroupType;

public class Group
    extends Account
{
    private GroupType groupType;

    public Group()
    {
        this( false );
    }

    public Group(boolean isRole)
    {
        super(isRole ? AccountType.ROLE : AccountType.GROUP);
    }

    public GroupType getGroupType()
    {
        return groupType;
    }

    public void setGroupType( GroupType groupType )
    {
        this.groupType = groupType;
    }
}
