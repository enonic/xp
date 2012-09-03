package com.enonic.wem.core.content.datatype;


import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.common.base.Preconditions;

public class DataTypes
{
    public static final MultiLinedString STRING = new MultiLinedString( 1 );

    public static final HtmlPart HTML_PART = new HtmlPart( 2 );

    public static final Xml XML = new Xml( 3 );

    public static final Date DATE = new Date( 4 );

    public static final Computed COMPUTED = new Computed( 5 );

    public static final WholeNumber WHOLE_NUMBER = new WholeNumber( 6 );

    public static final DecimalNumber DECIMAL_NUMBER = new DecimalNumber( 7 );

    public static final GeographicCoordinate GEOGRAPHIC_COORDINATE = new GeographicCoordinate( 8 );

    private static final Map<Integer, DataType> typesByKey = new HashMap<Integer, DataType>();

    static
    {
        register( STRING );
        register( HTML_PART );
        register( XML );
        register( DATE );
        register( COMPUTED );
        register( WHOLE_NUMBER );
        register( DECIMAL_NUMBER );
        register( GEOGRAPHIC_COORDINATE );
    }

    private static void register( DataType dataType )
    {
        Object previous = typesByKey.put( dataType.getKey(), dataType );
        Preconditions.checkState( previous == null, "DataType already registered: " + dataType.getKey() );
    }

    public static DataType parse( int key )
    {
        return typesByKey.get( key );
    }
}
