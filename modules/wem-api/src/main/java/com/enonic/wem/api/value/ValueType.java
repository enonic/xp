package com.enonic.wem.api.value;

public enum ValueType
{
    DATA( "Data" ),
    BOOLEAN( "Boolean" ),
    STRING( "String" ),
    XML( "Xml" ),
    HTML_PART( "HtmlPart" ),
    DOUBLE( "Double" ),
    LONG( "Long" ),
    CONTENT_ID( "ContentId" ),
    ENTITY_ID( "EntityId" ),
    GEO_POINT( "GeoPoint" ),
    DATE( "Date" ),
    DATE_TIME( "DateTime" );

    private final String name;

    private ValueType( final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}
