package com.enonic.wem.web.rest.account;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

public interface AccountModel extends Comparable<AccountModel>
{
    public final static String KEY = "key";

    public final static String USER_INFO = "userInfo";

    @JsonProperty("type")
    public String getAccountType();

    public void setKey( String key );

    public String getKey();

    public void setName( String name );

    public String getName();

    public void setQualifiedName( String qualifiedName );

    public String getQualifiedName();

    public void setUserStore( String userStore );

    public String getUserStore();

    public void setDisplayName( String displayName );

    public String getDisplayName();

    public void setLastModified( Date lastModified );

    public Date getLastModified();

    @JsonProperty("hasPhoto")
    public boolean hasPhoto();

    public void setBuiltIn( boolean builtIn );

    public boolean isBuiltIn();

    @JsonProperty("isEditable")
    public boolean isEditable();
}
