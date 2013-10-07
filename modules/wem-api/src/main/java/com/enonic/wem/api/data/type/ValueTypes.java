package com.enonic.wem.api.data.type;


import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;


public final class ValueTypes
{
    public static final Text TEXT = new Text( 1 );

    public static final BinaryId BINARY_ID = new BinaryId( 2 );

    public static final HtmlPart HTML_PART = new HtmlPart( 3 );

    public static final Xml XML = new Xml( 4 );

    public static final DateMidnight DATE_MIDNIGHT = new DateMidnight( 5 );

    public static final DateTime DATE_TIME = new DateTime( 6 );

    public static final ContentId CONTENT_ID = new ContentId( 7 );

    public static final Long LONG = new Long( 8 );

    public static final DecimalNumber DECIMAL_NUMBER = new DecimalNumber( 9 );

    public static final GeographicCoordinate GEOGRAPHIC_COORDINATE = new GeographicCoordinate( 10 );

    public static final AttachmentName ATTACHMENT_NAME = new AttachmentName( 11 );

    private static final Map<Integer, ValueType> typesByKey = new HashMap<>();

    private static final Map<String, ValueType> typesByName = new HashMap<>();

    static
    {
        register( TEXT );
        register( BINARY_ID );
        register( HTML_PART );
        register( XML );
        register( DATE_MIDNIGHT );
        register( DATE_TIME );
        register( CONTENT_ID );
        register( LONG );
        register( DECIMAL_NUMBER );
        register( GEOGRAPHIC_COORDINATE );
        register( ATTACHMENT_NAME );
    }

    private static void register( ValueType valueType )
    {
        Object previous = typesByKey.put( valueType.getKey(), valueType );
        Preconditions.checkState( previous == null, "ValueType already registered: " + valueType.getKey() );

        previous = typesByName.put( valueType.getName(), valueType );
        Preconditions.checkState( previous == null, "ValueType already registered: " + valueType.getName() );
    }

    public static ValueType parseByKey( int key )
    {
        return typesByKey.get( key );
    }

    public static ValueType parseByName( String name )
    {
        return typesByName.get( name );
    }
}
