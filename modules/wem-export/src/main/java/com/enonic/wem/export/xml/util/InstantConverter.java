package com.enonic.wem.export.xml.util;

import java.sql.Date;
import java.time.Instant;
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

    public static Instant convertToInstant( final XMLGregorianCalendar gregorianCalendar )
    {
        return gregorianCalendar.toGregorianCalendar().getTime().toInstant();
    }

}
