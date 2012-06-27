package com.enonic.wem.core.jcr.accounts;

import org.joda.time.DateTime;


public interface JcrAccount
{
    JcrAccountType getAccountType();

    void setId( String id );

    String getId();

    void setName( String name );

    String getName();

    void setQualifiedName( String qualifiedName );

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

    boolean isEditable();

    boolean isUser();

    boolean isGroup();

    boolean isRole();

}
