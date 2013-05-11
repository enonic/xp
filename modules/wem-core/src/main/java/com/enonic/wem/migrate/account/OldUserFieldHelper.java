package com.enonic.wem.migrate.account;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

final class OldUserFieldHelper
{
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "yyyyMMdd" );

    private final SimpleDateFormat dateFormat;

    private SimpleDateFormat[] getSupportedDateFormats()
    {
        SimpleDateFormat iso = new SimpleDateFormat( "yyyy-MM-dd" );
        iso.setLenient( false );

        SimpleDateFormat old = new SimpleDateFormat( "dd.MM.yyyy" );
        old.setLenient( false );

        SimpleDateFormat standard = DATE_FORMAT;
        standard.setLenient( false );

        return new SimpleDateFormat[]{iso, old, standard};
    }

    public OldUserFieldHelper()
    {
        this( null );
    }

    public OldUserFieldHelper( String format )
    {
        this.dateFormat = format != null ? new SimpleDateFormat( format ) : DATE_FORMAT;
    }

    public String toString( OldUserField field )
    {
        if ( field == null )
        {
            return null;
        }

        Object value = field.getValue();
        if ( value == null )
        {
            return null;
        }

        if ( value instanceof Date )
        {
            return formatDate( (Date) value );
        }

        if ( value instanceof Boolean )
        {
            return formatBoolean( (Boolean) value );
        }

        if ( value instanceof OldGender )
        {
            return formatGender( (OldGender) value );
        }

        if ( value instanceof Locale )
        {
            return formatLocale( (Locale) value );
        }

        if ( value instanceof TimeZone )
        {
            return formatTimezone( (TimeZone) value );
        }

        return value.toString();
    }

    public Object fromString( OldUserFieldType type, String value )
    {
        if ( value == null )
        {
            return null;
        }

        if ( type.isOfType( String.class ) )
        {
            return value;
        }

        if ( type.isOfType( Date.class ) )
        {
            return parseDate( value );
        }

        if ( type.isOfType( Boolean.class ) )
        {
            return parseBoolean( value );
        }

        if ( type.isOfType( OldGender.class ) )
        {
            return parseGender( value );
        }

        if ( type.isOfType( Locale.class ) )
        {
            return parseLocale( value );
        }

        if ( type.isOfType( TimeZone.class ) )
        {
            return parseTimeZone( value );
        }

        throw new IllegalArgumentException( "Convertion of type [" + type.getTypeClass().getName() + "] not supported" );
    }

    private String formatDate( Date value )
    {
        return value == null ? null : this.dateFormat.format( value );
    }

    private String formatBoolean( Boolean value )
    {
        return value.toString();
    }

    private String formatGender( OldGender value )
    {
        return value.toString().toLowerCase();
    }

    private String formatLocale( Locale value )
    {
        return value.toString();
    }

    private String formatTimezone( TimeZone value )
    {
        return value.getID();
    }

    private Boolean parseBoolean( String value )
    {
        if ( StringUtils.isBlank( value ) )
        {
            return null;
        }

        return "|1|true|on".indexOf( value ) > 0;
    }

    private OldGender parseGender( String value )
    {
        if ( StringUtils.isBlank( value ) )
        {
            return null;
        }

        if ( value.equalsIgnoreCase( "m" ) || value.equalsIgnoreCase( "male" ) )
        {
            return OldGender.MALE;
        }

        if ( value.equalsIgnoreCase( "f" ) || value.equalsIgnoreCase( "female" ) )
        {
            return OldGender.FEMALE;
        }

        return null;
    }

    private Locale parseLocale( final String value )
    {
        if ( StringUtils.isBlank( value ) )
        {
            return null;
        }

        return OldLocaleParser.parseLocale( value );
    }

    private TimeZone parseTimeZone( String value )
    {
        if ( StringUtils.isBlank( value ) )
        {
            return null;
        }

        return TimeZone.getTimeZone( value );
    }

    private Date parseDate( String value )
    {
        if ( StringUtils.isEmpty( value ) )
        {
            return null;
        }

        for ( SimpleDateFormat format : getSupportedDateFormats() )
        {
            Date date = parseDate( value, format );
            if ( date != null )
            {
                return date;
            }
        }
        throw new IllegalArgumentException( "Could not parse date " + value );
    }

    private Date parseDate( String value, SimpleDateFormat format )
    {
        try
        {
            return format.parse( value );
        }
        catch ( Exception e )
        {
            return null;
        }
    }
}
