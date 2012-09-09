package com.enonic.wem.api.userstore.config;

public final class UserStoreFieldConfig
{
    private final String name;

    private boolean required;

    private boolean readOnly;

    private boolean remote;

    private boolean iso;

    public UserStoreFieldConfig( final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean isRequired()
    {
        return required;
    }

    public void setRequired( final boolean required )
    {
        this.required = required;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly( final boolean readOnly )
    {
        this.readOnly = readOnly;
    }

    public boolean isRemote()
    {
        return remote;
    }

    public void setRemote( final boolean remote )
    {
        this.remote = remote;
    }

    public boolean isIso()
    {
        return iso;
    }

    public void setIso( final boolean iso )
    {
        this.iso = iso;
    }
}
