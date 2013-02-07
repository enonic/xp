package com.enonic.wem.core.search.content;

public enum ContentIndexField
{
    FIELD_SEPARATOR( "_" ),
    TYPE_SEPARATOR( "." ),
    KEY_FIELD( "key" ),
    DISPLAY_NAME_FIELD( "displayName" ),
    LAST_MODIFIED_FIELD( "lastModified" ),
    CREATED_FIELD( "created" ),
    CONTENT_TYPE_NAME_FIELD( "contentType" ),
    OWNER_FIELD( "owner" ),
    MODIFIER_FIELD( "modifier" ),
    CONTENT_DATA_PREFIX( "data" );

    private final String id;

    private ContentIndexField( final String id )
    {
        this.id = id;
    }

    public String id()
    {
        return this.id;
    }

}
