package com.enonic.cms.web.rest.userstore;

public final class UserStoreConnectorModel
{

    private String name;

    private String pluginType;

    private boolean canCreateUser;

    private boolean canUpdateUser;

    private boolean canUpdateUserPassword;

    private boolean canDeleteUser;

    private boolean canCreateGroup;

    private boolean canUpdateGroup;

    private boolean canReadGroup;

    private boolean canDeleteGroup;

    private boolean groupsLocal;


    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getPluginType()
    {
        return pluginType;
    }

    public void setPluginType( String pluginType )
    {
        this.pluginType = pluginType;
    }

    public boolean isCanCreateUser()
    {
        return canCreateUser;
    }

    public void setCanCreateUser( boolean canCreateUser )
    {
        this.canCreateUser = canCreateUser;
    }

    public boolean isCanUpdateUser()
    {
        return canUpdateUser;
    }

    public void setCanUpdateUser( boolean canUpdateUser )
    {
        this.canUpdateUser = canUpdateUser;
    }

    public boolean isCanUpdateUserPassword()
    {
        return canUpdateUserPassword;
    }

    public void setCanUpdateUserPassword( boolean canUpdateUserPassword )
    {
        this.canUpdateUserPassword = canUpdateUserPassword;
    }

    public boolean isCanDeleteUser()
    {
        return canDeleteUser;
    }

    public void setCanDeleteUser( boolean canDeleteUser )
    {
        this.canDeleteUser = canDeleteUser;
    }

    public boolean isCanCreateGroup()
    {
        return canCreateGroup;
    }

    public void setCanCreateGroup( boolean canCreateGroup )
    {
        this.canCreateGroup = canCreateGroup;
    }

    public boolean isCanUpdateGroup()
    {
        return canUpdateGroup;
    }

    public void setCanUpdateGroup( boolean canUpdateGroup )
    {
        this.canUpdateGroup = canUpdateGroup;
    }

    public boolean isCanReadGroup()
    {
        return canReadGroup;
    }

    public void setCanReadGroup( boolean canReadGroup )
    {
        this.canReadGroup = canReadGroup;
    }

    public boolean isCanDeleteGroup()
    {
        return canDeleteGroup;
    }

    public void setCanDeleteGroup( boolean canDeleteGroup )
    {
        this.canDeleteGroup = canDeleteGroup;
    }

    public boolean isGroupsLocal()
    {
        return groupsLocal;
    }

    public void setGroupsLocal( boolean groupsLocal )
    {
        this.groupsLocal = groupsLocal;
    }
}
