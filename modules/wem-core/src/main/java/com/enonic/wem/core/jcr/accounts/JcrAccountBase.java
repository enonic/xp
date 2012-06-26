package com.enonic.wem.core.jcr.accounts;

import org.joda.time.DateTime;

abstract class JcrAccountBase
        implements JcrAccount
{
    private final JcrAccountType type;

    private String id;

    private String name;

    private String qualifiedName;

    private String displayName;

    private String userStore;

    private DateTime lastModified;

    private boolean builtIn;

    private boolean editable;


    protected JcrAccountBase( JcrAccountType type )
    {
        this.type = type;
    }

    public boolean isUser()
    {
        return this.type == JcrAccountType.USER;
    }

    public boolean isGroup()
    {
        return this.type == JcrAccountType.GROUP;
    }

    public boolean isRole()
    {
        return this.type == JcrAccountType.ROLE;
    }

    public JcrAccountType getAccountType()
    {
        return this.type;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getQualifiedName()
    {
        return qualifiedName;
    }

    public void setQualifiedName( String qualifiedName )
    {
        this.qualifiedName = qualifiedName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public String getUserStore()
    {
        return userStore;
    }

    public void setUserStore( String userStore )
    {
        this.userStore = userStore;
    }

    public DateTime getLastModified()
    {
        return lastModified;
    }

    public void setLastModified( DateTime lastModified )
    {
        this.lastModified = lastModified;
    }

    public boolean isBuiltIn()
    {
        return builtIn;
    }

    public void setBuiltIn( boolean builtIn )
    {
        this.builtIn = builtIn;
    }

    public boolean isEditable()
    {
        return editable;
    }

    public void setEditable( boolean editable )
    {
        this.editable = editable;
    }

}
