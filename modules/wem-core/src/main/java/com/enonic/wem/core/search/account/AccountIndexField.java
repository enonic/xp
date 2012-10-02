package com.enonic.wem.core.search.account;

public enum AccountIndexField
{
    DISPLAY_NAME_FIELD( "displayName" ),
    LAST_MODIFIED_FIELD( "lastModified" ),
    TYPE_FIELD( "type" ),
    USERSTORE_FIELD( "userstore" ),
    NAME_FIELD( "name" ),
    FIRST_NAME_FIELD( "first-name" ),
    KEY_FIELD( "key" ),
    EMAIL_FIELD( "email" ),
    ORGANIZATION_FIELD( "organization" ),
    MEMBERS_FIELD( "members" )
    ;

    private final String id;

    AccountIndexField( String id )
    {
        this.id = id;
    }

    public String id()
    {
        return this.id;
    }

    public String notAnalyzedId()
    {
        return this.id + ".untouched";
    }

    public String lowerCaseId()
    {
        return this.id + ".lowercase";
    }

    public static AccountIndexField parse( String id )
    {
        if ( id == null )
        {
            return null;
        }
        id = id.toLowerCase();
        for ( AccountIndexField field : AccountIndexField.values() )
        {
            if ( field.id.toLowerCase().equals( id ) )
            {
                return field;
            }
        }
        return null;

    }
}
