package com.enonic.wem.core.search.content;

public enum ContentIndexField
{
    FIELD_SEPARATOR( "_" ),
    TYPE_SEPARATOR( "." ),
    KEY( "key" ),
    DISPLAY_NAME( "displayName" ),
    LAST_MODIFIED( "lastModified" ),
    CREATED( "created" ),
    CONTENT_TYPE( "contentType" ),
    OWNER( "owner" ),
    MODIFIER( "modifier" ),
    CONTENT_DATA_PREFIX( "data" ),
    PATH( "path" );

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
