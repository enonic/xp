package com.enonic.wem.core.jcr.old.accounts;

import org.joda.time.DateTime;


public interface JcrAccount
{
    JcrAccountType getAccountType();

    void setId( String id );

    String getId();

    void setName( String name );

    String getName();

    String getQualifiedName();

    void setUserStore( String userStore );

    String getUserStore();

    void setDisplayName( String displayName );

    String getDisplayName();

    void setLastModified( DateTime lastModified );

    DateTime getLastModified();

    boolean hasPhoto();

    void setBuiltIn( boolean builtIn );

    boolean isBuiltIn();

    boolean isUser();

    boolean isGroup();

    boolean isRole();

}
