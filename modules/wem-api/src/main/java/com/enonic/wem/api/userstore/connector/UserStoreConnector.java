package com.enonic.wem.api.userstore.connector;

public final class UserStoreConnector
{
    private final String name;

    private boolean createUser;

    private boolean updateUser;

    private boolean updatePassword;

    private boolean deleteUser;

    private boolean createGroup;

    private boolean readGroup;

    private boolean updateGroup;

    private boolean deleteGroup;

    private boolean groupsStoredRemote;

    private boolean resurrectDeletedUsers;

    private boolean resurrectDeletedGroups;

    private String pluginClass;

    public UserStoreConnector( final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public boolean isCreateUser()
    {
        return createUser;
    }

    public boolean isUpdateUser()
    {
        return updateUser;
    }

    public boolean isUpdatePassword()
    {
        return updatePassword;
    }

    public boolean isDeleteUser()
    {
        return deleteUser;
    }

    public boolean isCreateGroup()
    {
        return createGroup;
    }

    public boolean isReadGroup()
    {
        return readGroup;
    }

    public boolean isUpdateGroup()
    {
        return updateGroup;
    }

    public boolean isDeleteGroup()
    {
        return deleteGroup;
    }

    public boolean isGroupsStoredRemote()
    {
        return groupsStoredRemote;
    }

    public boolean isResurrectDeletedUsers()
    {
        return resurrectDeletedUsers;
    }

    public boolean isResurrectDeletedGroups()
    {
        return resurrectDeletedGroups;
    }

    public String getPluginClass()
    {
        return pluginClass;
    }

    public void setCreateUser( final boolean createUser )
    {
        this.createUser = createUser;
    }

    public void setUpdateUser( final boolean updateUser )
    {
        this.updateUser = updateUser;
    }

    public void setUpdatePassword( final boolean updatePassword )
    {
        this.updatePassword = updatePassword;
    }

    public void setDeleteUser( final boolean deleteUser )
    {
        this.deleteUser = deleteUser;
    }

    public void setCreateGroup( final boolean createGroup )
    {
        this.createGroup = createGroup;
    }

    public void setReadGroup( final boolean readGroup )
    {
        this.readGroup = readGroup;
    }

    public void setUpdateGroup( final boolean updateGroup )
    {
        this.updateGroup = updateGroup;
    }

    public void setDeleteGroup( final boolean deleteGroup )
    {
        this.deleteGroup = deleteGroup;
    }

    public void setGroupsStoredRemote( final boolean groupsStoredRemote )
    {
        this.groupsStoredRemote = groupsStoredRemote;
    }

    public void setResurrectDeletedUsers( final boolean resurrectDeletedUsers )
    {
        this.resurrectDeletedUsers = resurrectDeletedUsers;
    }

    public void setResurrectDeletedGroups( final boolean resurrectDeletedGroups )
    {
        this.resurrectDeletedGroups = resurrectDeletedGroups;
    }

    public void setPluginClass( final String pluginClass )
    {
        this.pluginClass = pluginClass;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final UserStoreConnector that = (UserStoreConnector) o;

        if ( !name.equals( that.name ) )
        {
            return false;
        }
        if ( !pluginClass.equals( that.pluginClass ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = name.hashCode();
        result = 31 * result + pluginClass.hashCode();
        return result;
    }
}
