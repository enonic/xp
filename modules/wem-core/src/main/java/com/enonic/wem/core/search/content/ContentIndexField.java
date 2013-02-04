package com.enonic.wem.core.search.content;

public enum ContentIndexField
{
    KEY_FIELD( "key" ),
    DISPLAY_NAME_FIELD( "displayName" ),
    LAST_MODIFIED_FIELD( "lastModified" ),
    CREATED_FIELD( "created" ),
    CONTENT_TYPE_NAME_FIELD( "contentType" ),
    OWNER_FIELD( "owner" ),
    MODIFIER_FIELD( "modifier" );

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
