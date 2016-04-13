package com.enonic.xp.site.mapping;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueType;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.schema.mixin.MixinName;

import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.substringBefore;

@Beta
public final class ContentMappingConstraint
{
    private static final String SEPARATOR = ":";

    private static final String ID_PROPERTY = "_id";

    private static final String NAME_PROPERTY = "_name";

    private static final String PATH_PROPERTY = "_path";

    private static final String TYPE_PROPERTY = "type";

    private static final String DISPLAY_NAME_PROPERTY = "displayName";

    private static final String HAS_CHILDREN_PROPERTY = "hasChildren";

    private static final String LANGUAGE_PROPERTY = "language";

    private static final String VALID_PROPERTY = "valid";

    private static final String DATA_PROPERTY_PREFIX = "data.";

    private static final String XDATA_PROPERTY_PREFIX = "x.";

    private final String id;

    private final String value;

    private ContentMappingConstraint( final String id, final String value )
    {
        this.id = id;
        this.value = value;
    }

    public boolean matches( final Content content )
    {
        final String val = trimQuotes( this.value );
        if ( ID_PROPERTY.equals( this.id ) )
        {
            return valueMatches( val, content.getId().toString() );
        }
        else if ( NAME_PROPERTY.equals( this.id ) )
        {
            return valueMatches( val, content.getName().toString() );
        }
        else if ( PATH_PROPERTY.equals( this.id ) )
        {
            return valueMatches( val, content.getPath().toString() );
        }
        else if ( TYPE_PROPERTY.equals( this.id ) )
        {
            return valueMatches( val, content.getType().toString() );
        }
        else if ( DISPLAY_NAME_PROPERTY.equals( this.id ) )
        {
            return valueMatches( val, content.getDisplayName() );
        }
        else if ( HAS_CHILDREN_PROPERTY.equals( this.id ) )
        {
            return valueMatches( val, content.hasChildren() );
        }
        else if ( LANGUAGE_PROPERTY.equals( this.id ) )
        {
            return valueMatches( val, content.getLanguage() == null ? "" : content.getLanguage().toLanguageTag() );
        }
        else if ( VALID_PROPERTY.equals( this.id ) )
        {
            return valueMatches( val, content.isValid() );
        }
        else if ( this.id.startsWith( DATA_PROPERTY_PREFIX ) )
        {
            final String dataPath = substringAfter( id, DATA_PROPERTY_PREFIX );
            final Property prop = content.getData().getProperty( dataPath );
            if ( prop == null || prop.getValue() == null )
            {
                return false;
            }

            final Value propertyValue = convert( val, prop.getValue().getType() );
            return propertyValue != null && valueMatches( propertyValue.asString(), prop.getValue().asString() );
        }
        else if ( this.id.startsWith( XDATA_PROPERTY_PREFIX ) )
        {
            String dataPath = substringAfter( id, XDATA_PROPERTY_PREFIX );
            final String appPrefix = substringBefore( dataPath, "." );
            dataPath = substringAfter( dataPath, "." );
            final String mixinName = substringBefore( dataPath, "." );
            dataPath = substringAfter( dataPath, "." );
            final PropertyTree xData = getXData( content.getAllExtraData(), appPrefix, mixinName );
            if ( xData == null )
            {
                return false;
            }

            final Property prop = xData.getProperty( dataPath );
            if ( prop == null || prop.getValue() == null )
            {
                return false;
            }

            final Value propertyValue = convert( val, prop.getValue().getType() );
            return propertyValue != null && valueMatches( propertyValue.asString(), prop.getValue().asString() );
        }

        return false;
    }

    private PropertyTree getXData( final ExtraDatas xDatas, final String appPrefix, final String name )
    {
        if ( xDatas == null )
        {
            return null;
        }
        try
        {
            final ApplicationKey app = ExtraData.fromApplicationPrefix( appPrefix );
            final MixinName mixinName = MixinName.from( app, name );
            final ExtraData extraData = xDatas.getMetadata( mixinName );
            if ( extraData == null )
            {
                return null;
            }
            return extraData.getData();
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    private boolean valueMatches( final String pattern, final boolean value )
    {
        return valueMatches( pattern, Boolean.toString( value ) );
    }

    private boolean valueMatches( final String pattern, final String value )
    {
        try
        {
            return Pattern.compile( pattern ).matcher( value ).matches();
        }
        catch ( PatternSyntaxException e )
        {
            return false;
        }
    }

    private Value convert( final String value, final ValueType type )
    {
        if ( type == ValueTypes.XML )
        {
            return ValueFactory.newXml( ValueTypes.XML.convert( value ) );
        }
        if ( type == ValueTypes.LOCAL_DATE )
        {
            return ValueFactory.newLocalDate( ValueTypes.LOCAL_DATE.convert( value ) );
        }
        if ( type == ValueTypes.LOCAL_TIME )
        {
            return ValueFactory.newLocalTime( ValueTypes.LOCAL_TIME.convert( value ) );
        }
        if ( type == ValueTypes.LOCAL_DATE_TIME )
        {
            return ValueFactory.newLocalDateTime( ValueTypes.LOCAL_DATE_TIME.convert( value ) );
        }
        if ( type == ValueTypes.DATE_TIME )
        {
            return ValueFactory.newDateTime( ValueTypes.DATE_TIME.convert( value ) );
        }
        if ( type == ValueTypes.LONG )
        {
            return ValueFactory.newLong( ValueTypes.LONG.convert( value ) );
        }
        if ( type == ValueTypes.DOUBLE )
        {
            return ValueFactory.newDouble( ValueTypes.DOUBLE.convert( value ) );
        }
        if ( type == ValueTypes.GEO_POINT )
        {
            return ValueFactory.newGeoPoint( ValueTypes.GEO_POINT.convert( value ) );
        }
        if ( type == ValueTypes.BOOLEAN )
        {
            return ValueFactory.newBoolean( ValueTypes.BOOLEAN.convert( value ) );
        }
        if ( type == ValueTypes.REFERENCE )
        {
            return ValueFactory.newReference( ValueTypes.REFERENCE.convert( value ) );
        }
        if ( type == ValueTypes.LINK )
        {
            return ValueFactory.newLink( ValueTypes.LINK.convert( value ) );
        }
        if ( type == ValueTypes.BINARY_REFERENCE )
        {
            return ValueFactory.newBinaryReference( ValueTypes.BINARY_REFERENCE.convert( value ) );
        }
        return ValueFactory.newString( value );
    }

    private String trimQuotes( final String value )
    {
        final int length = value.length();
        if ( ( length > 1 ) && ( value.charAt( 0 ) == '\'' ) && ( value.charAt( length - 1 ) == '\'' ) )
        {
            return value.substring( 1, length - 1 );
        }
        else
        {
            return value;
        }
    }

    public static ContentMappingConstraint parse( final String expression )
    {
        if ( !expression.contains( SEPARATOR ) )
        {
            throw new IllegalArgumentException( "Invalid match expression: " + expression );
        }
        final String id = substringBefore( expression, SEPARATOR ).trim();
        final String value = substringAfter( expression, SEPARATOR ).trim();
        if ( StringUtils.isBlank( id ) )
        {
            throw new IllegalArgumentException( "Invalid match expression: " + expression );
        }
        return new ContentMappingConstraint( id, value );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final ContentMappingConstraint that = (ContentMappingConstraint) o;
        return Objects.equals( id, that.id ) && Objects.equals( value, that.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, value );
    }

    @Override
    public String toString()
    {
        return id + SEPARATOR + value;
    }
}
