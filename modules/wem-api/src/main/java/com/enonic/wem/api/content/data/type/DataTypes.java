package com.enonic.wem.api.content.data.type;


import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;


public final class DataTypes
{
    public static final Set SET = new Set( 0 );

    public static final Text TEXT = new Text( 1 );

    public static final BinaryReference BINARY_REFERENCE = new BinaryReference( 2 );

    public static final HtmlPart HTML_PART = new HtmlPart( 3 );

    public static final Xml XML = new Xml( 4 );

    public static final DateMidnight DATE_MIDNIGHT = new DateMidnight( 5 );

    public static final ContentReference CONTENT_REFERENCE = new ContentReference( 6 );

    public static final WholeNumber WHOLE_NUMBER = new WholeNumber( 7 );

    public static final DecimalNumber DECIMAL_NUMBER = new DecimalNumber( 8 );

    public static final GeographicCoordinate GEOGRAPHIC_COORDINATE = new GeographicCoordinate( 9 );

    private static final Map<Integer, DataType> typesByKey = new HashMap<Integer, DataType>();

    private static final Map<String, DataType> typesByName = new HashMap<String, DataType>();

    static
    {
        register( SET );
        register( TEXT );
        register( BINARY_REFERENCE );
        register( HTML_PART );
        register( XML );
        register( DATE_MIDNIGHT );
        register( CONTENT_REFERENCE );
        register( WHOLE_NUMBER );
        register( DECIMAL_NUMBER );
        register( GEOGRAPHIC_COORDINATE );
    }

    private static void register( DataType dataType )
    {
        Object previous = typesByKey.put( dataType.getKey(), dataType );
        Preconditions.checkState( previous == null, "DataType already registered: " + dataType.getKey() );

        previous = typesByName.put( dataType.getName(), dataType );
        Preconditions.checkState( previous == null, "DataType already registered: " + dataType.getName() );
    }

    public static DataType parseByKey( int key )
    {
        return typesByKey.get( key );
    }

    public static DataType parseByName( String name )
    {
        return typesByName.get( name );
    }
}
