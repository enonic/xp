package com.enonic.wem.export.internal.xml.util;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.enonic.wem.export.ExportNodeException;

public class InstantConverter
{

    public static XMLGregorianCalendar convertToXmlSerializable( final Instant instant )
    {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime( Date.from( instant ) );

        try
        {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar( c );
        }
        catch ( DatatypeConfigurationException e )
        {
            throw new ExportNodeException( e );
        }
    }

    public static XMLGregorianCalendar convertToXmlSerializable( final LocalTime localTime )
    {
        try
        {
            return DatatypeFactory.newInstance().
                newXMLGregorianCalendarTime( localTime.getHour(), localTime.getMinute(), localTime.getSecond(), localTime.getNano() );
        }
        catch ( DatatypeConfigurationException e )
        {
            throw new ExportNodeException( e );
        }
    }

    public static XMLGregorianCalendar convertToXmlSerializable( final LocalDate localDate )
    {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime( Date.valueOf( localDate ) );

        try
        {
            return DatatypeFactory.newInstance().newXMLGregorianCalendarDate( cal.get( Calendar.YEAR ), cal.get( Calendar.MONTH ) + 1,
                                                                              cal.get( Calendar.DAY_OF_MONTH ), 0 );
        }
        catch ( DatatypeConfigurationException e )
        {
            throw new ExportNodeException( e );
        }
    }

    public static XMLGregorianCalendar convertToXmlSerializable( final LocalDateTime localDateTime )
    {
        GregorianCalendar c = new GregorianCalendar();
        c.set( localDateTime.getYear(), localDateTime.getMonthValue() + 1, localDateTime.getDayOfMonth(), localDateTime.getHour(),
               localDateTime.getMinute(), localDateTime.getSecond() );

        try
        {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar( c );
        }
        catch ( DatatypeConfigurationException e )
        {
            throw new ExportNodeException( e );
        }
    }


    public static Instant convertToInstant( final XMLGregorianCalendar gregorianCalendar )
    {
        return gregorianCalendar.toGregorianCalendar().getTime().toInstant();
    }

}
