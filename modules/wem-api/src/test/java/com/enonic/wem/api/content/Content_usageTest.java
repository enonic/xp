package com.enonic.wem.api.content;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

import static com.enonic.wem.api.data.DataSet.newDataSet;
import static com.enonic.wem.api.data.type.ValueTypes.DOUBLE;
import static com.enonic.wem.api.data.type.ValueTypes.HTML_PART;
import static com.enonic.wem.api.data.type.ValueTypes.LOCAL_DATE;
import static com.enonic.wem.api.data.type.ValueTypes.LOCAL_DATE_TIME;
import static com.enonic.wem.api.data.type.ValueTypes.LOCAL_TIME;
import static com.enonic.wem.api.data.type.ValueTypes.LONG;
import static com.enonic.wem.api.data.type.ValueTypes.STRING;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class Content_usageTest
{
    @Test
    public void dataSet_setProperty()
    {
        DataSet dataSet = new ContentData();

        // exercise
        dataSet.setProperty( "myText", Value.newString( "abc" ) );
        dataSet.setProperty( "myNum", Value.newLong( 123 ) );
        dataSet.setProperty( "myDate", Value.newLocalDate( LocalDate.of( 2013, 1, 13 ) ) );
        dataSet.setProperty( "myDec", Value.newDouble( 123.123 ) );
        dataSet.setProperty( "myHtml", Value.newHtmlPart( "<p>abc</p>" ) );

        // verify
        assertEquals( STRING, dataSet.getProperty( "myText" ).getValueType() );
        assertEquals( LONG, dataSet.getProperty( "myNum" ).getValueType() );
        assertEquals( LOCAL_DATE, dataSet.getProperty( "myDate" ).getValueType() );
        assertEquals( DOUBLE, dataSet.getProperty( "myDec" ).getValueType() );
        assertEquals( HTML_PART, dataSet.getProperty( "myHtml" ).getValueType() );
    }

    @Test
    public void dataSet_setProperty_with_array_of_Values()
    {
        DataSet dataSet = new ContentData();

        // exercise
        dataSet.setProperty( "myText", Value.newString( "aaa" ), Value.newString( "bbb" ), Value.newString( "ccc" ) );

        // verify
        assertEquals( "aaa", dataSet.getProperty( "myText" ).getString( 0 ) );
        assertEquals( "bbb", dataSet.getProperty( "myText" ).getString( 1 ) );
        assertEquals( "ccc", dataSet.getProperty( "myText" ).getString( 2 ) );
    }

    @Test
    public void dataSet_setProperty_with_DataPath_containing_DataSet()
    {
        DataSet dataSet = new ContentData();

        // exercise
        dataSet.setProperty( "mySet.myText", Value.newString( "abc" ) );
        dataSet.setProperty( "mySet.myNum", Value.newLong( 123 ) );
        dataSet.setProperty( "mySet.myDate", Value.newLocalDate( LocalDate.of( 2013, 1, 13 ) ) );
        dataSet.setProperty( "mySet.myDec", Value.newDouble( 123.123 ) );
        dataSet.setProperty( "mySet.myHtml", Value.newHtmlPart( "<p>abc</p>" ) );

        // verify
        assertEquals( "mySet.myText", dataSet.getProperty( "mySet.myText" ).getPath().toString() );
        assertEquals( "mySet.myNum", dataSet.getProperty( "mySet.myNum" ).getPath().toString() );
        assertEquals( "mySet.myDate", dataSet.getProperty( "mySet.myDate" ).getPath().toString() );
        assertEquals( "mySet.myDec", dataSet.getProperty( "mySet.myDec" ).getPath().toString() );
        assertEquals( "mySet.myHtml", dataSet.getProperty( "mySet.myHtml" ).getPath().toString() );
    }

    @Test
    public void dataSet_add_Data_using_new_PropertyType()
    {
        DataSet dataSet = new ContentData();

        // exercise
        dataSet.add( Property.newString( "myText", "abc" ) );
        dataSet.add( Property.newLong( "myNum", 123L ) );
        dataSet.add( Property.newDouble( "myDec", 123.123 ) );
        dataSet.add( Property.newLocalDate( "myDate", LocalDate.of( 2013, 1, 13 ) ) );
        dataSet.add( Property.newLocalTime( "myTime", LocalTime.of( 10, 45, 55 ) ) );
        dataSet.add( Property.newLocalDateTime( "myLocalDateTime", LocalDateTime.of( 2014, 07, 14, 22, 45 ) ) );
        dataSet.add( Property.newHtmlPart( "myHtml", "<p>abc</p>" ) );

        // verify
        assertEquals( STRING, dataSet.getProperty( "myText" ).getValueType() );
        assertEquals( LONG, dataSet.getProperty( "myNum" ).getValueType() );
        assertEquals( LOCAL_DATE, dataSet.getProperty( "myDate" ).getValueType() );
        assertEquals( LOCAL_TIME, dataSet.getProperty( "myTime" ).getValueType() );
        assertEquals( LOCAL_DATE_TIME, dataSet.getProperty( "myLocalDateTime" ).getValueType() );
        assertEquals( DOUBLE, dataSet.getProperty( "myDec" ).getValueType() );
        assertEquals( HTML_PART, dataSet.getProperty( "myHtml" ).getValueType() );
    }

    @Test
    public void dataSet_addProperty()
    {
        DataSet dataSet = new ContentData();

        // exercise
        dataSet.addProperty( "myText", Value.newString( "abc" ) );
        dataSet.addProperty( "myNum", Value.newLong( 123 ) );
        dataSet.addProperty( "myDec", Value.newDouble( 123.123 ) );
        dataSet.addProperty( "myDate", Value.newLocalDate( LocalDate.of( 2013, 1, 13 ) ) );
        dataSet.addProperty( "myLocalTime", Value.newLocalTime( LocalTime.of( 10, 45, 59 ) ) );
        dataSet.addProperty( "myLocalDateTime", Value.newLocalDateTime( LocalDateTime.of( 2014, 8, 12, 10, 45, 59 ) ) );
        dataSet.addProperty( "myHtml", Value.newHtmlPart( "<p>abc</p>" ) );

        // verify
        assertEquals( STRING, dataSet.getProperty( "myText" ).getValueType() );
        assertEquals( LONG, dataSet.getProperty( "myNum" ).getValueType() );
        assertEquals( LOCAL_DATE, dataSet.getProperty( "myDate" ).getValueType() );
        assertEquals( DOUBLE, dataSet.getProperty( "myDec" ).getValueType() );
        assertEquals( HTML_PART, dataSet.getProperty( "myHtml" ).getValueType() );
        assertEquals( LOCAL_TIME, dataSet.getProperty( "myLocalTime" ).getValueType() );
        assertEquals( LOCAL_DATE_TIME, dataSet.getProperty( "myLocalDateTime" ).getValueType() );

    }

    @Test
    public void dataSet_addProperty_with_DataPath_containing_DataSet()
    {
        ContentData contentData = new ContentData();

        // exercise
        contentData.addProperty( "mySet.myText", Value.newString( "abc" ) );
        contentData.addProperty( "mySet.myNum", Value.newLong( 123 ) );
        contentData.addProperty( "mySet.myDec", Value.newDouble( 123.123 ) );
        contentData.addProperty( "mySet.myDate", Value.newLocalDate( LocalDate.of( 2013, 1, 13 ) ) );
        contentData.addProperty( "mySet.myLocalTime", Value.newLocalTime( LocalTime.of( 10, 45, 59 ) ) );
        contentData.addProperty( "mySet.myLocalDateTime", Value.newLocalDateTime( LocalDateTime.of( 2015, 8, 12, 10, 45, 59 ) ) );
        contentData.addProperty( "mySet.myHtml", Value.newHtmlPart( "<p>abc</p>" ) );

        // verify
        assertEquals( "mySet.myText", contentData.getProperty( "mySet.myText" ).getPath().toString() );
        assertEquals( "mySet.myNum", contentData.getProperty( "mySet.myNum" ).getPath().toString() );
        assertEquals( "mySet.myDate", contentData.getProperty( "mySet.myDate" ).getPath().toString() );
        assertEquals( "mySet.myDec", contentData.getProperty( "mySet.myDec" ).getPath().toString() );
        assertEquals( "mySet.myHtml", contentData.getProperty( "mySet.myHtml" ).getPath().toString() );
        assertEquals( "mySet.myLocalTime", contentData.getProperty( "mySet.myLocalTime" ).getPath().toString() );
        assertEquals( "mySet.myLocalDateTime", contentData.getProperty( "mySet.myLocalDateTime" ).getPath().toString() );
    }

    @Test
    public void dataSet_add_DataSet()
    {
        DataSet contentData = new ContentData();

        // exercise
        contentData.add( newDataSet().name( "mySet" ).build() );

        // verify
        assertNotNull( contentData.getDataSet( "mySet" ) );
    }

    @Test
    public void data_getValue()
    {
        DataSet dataSet = new ContentData();
        dataSet.add( Property.newString( "myText", "abc" ) );
        dataSet.add( Property.newLong( "myNum", 123L ) );
        dataSet.add( Property.newDouble( "myDec", 123.123 ) );
        dataSet.add( Property.newLocalDate( "myDate", LocalDate.of( 2013, 1, 13 ) ) );
        dataSet.add( Property.newLocalTime( "myLocalTime", LocalTime.of( 10, 45, 59 ) ) );
        dataSet.add( Property.newLocalDateTime( "myLocalDateTime", LocalDateTime.of( 2014, 8, 12, 10, 45, 59 ) ) );
        dataSet.add( Property.newHtmlPart( "myHtml", "<p>abc</p>" ) );

        // exercise & verify
        assertEquals( "abc", dataSet.getProperty( "myText" ).getString() );
        assertEquals( new Long( 123L ), dataSet.getProperty( "myNum" ).getLong() );
        assertEquals( 123.123, dataSet.getProperty( "myDec" ).getDouble() );
        assertEquals( LocalDate.of( 2013, 1, 13 ), dataSet.getProperty( "myDate" ).getLocalDate() );
        assertEquals( LocalTime.of( 10, 45, 59 ), dataSet.getProperty( "myLocalTime" ).getLocalTime() );
        assertEquals( LocalDateTime.of( 2014, 8, 12, 10, 45, 59 ), dataSet.getProperty( "myLocalDateTime" ).getLocalDateTime() );
        assertEquals( "<p>abc</p>", dataSet.getProperty( "myHtml" ).getString() );
    }

    @Test
    public void dataSet_add_Data_array()
    {
        DataSet dataSet = new ContentData();

        // exercise
        dataSet.add( Property.newString( "myText", "a" ) );
        dataSet.add( Property.newString( "myText", "b" ) );
        dataSet.add( Property.newString( "myText", "c" ) );

        // verify
        assertEquals( "a", dataSet.getProperty( "myText" ).getString( 0 ) );
        assertEquals( "b", dataSet.getProperty( "myText" ).getString( 1 ) );
        assertEquals( "c", dataSet.getProperty( "myText" ).getString( 2 ) );
    }

    @Test
    public void invoice_newProperty()
    {
        Invoice invoice = new Invoice();
        invoice.invoiceDate = Instant.now();
        invoice.recipient = "Runar Myklebust";
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 4mm", 120.00 ) );
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 10mm", 120.00 ) );

        ContentData contentData = new ContentData();
        contentData.add( Property.newLocalDate( "invoiceDate", LocalDateTime.ofInstant( invoice.invoiceDate,
                                                                                        ZoneOffset.UTC ).toLocalDate() ) );//invoice.invoiceDate.toLocalDate() ) );
        contentData.add( Property.newString( "recipient", invoice.recipient ) );

        for ( InvoiceLine line : invoice.lines )
        {
            DataSet invoiceLine = DataSet.newDataSet().name( "invoiceLine" ).build();
            invoiceLine.add( Property.newString( "text", line.text ) );
            invoiceLine.add( new Property( "money", resolveValue( line.money ) ) );

            contentData.add( invoiceLine );
        }

        // print out
        System.out.println( contentData.getProperty( "invoiceDate" ).getLocalDate() );
        System.out.println( contentData.getProperty( "recipient" ).getString() );
        for ( Data invoiceLine : contentData.getDataSet( "invoiceLine" ).getArray() )
        {
            DataSet invoiceLineDS = invoiceLine.toDataSet();
            System.out.println( invoiceLineDS.getProperty( "text" ).getString() + ": " + invoiceLineDS.getProperty( "money" ).getString() );
        }
    }

    @Test
    public void invoice_newType()
    {
        Invoice invoice = new Invoice();
        invoice.invoiceDate = Instant.now();
        invoice.recipient = "Runar Myklebust";
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 4mm", 120.00 ) );
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 10mm", 120.00 ) );

        ContentData contentData = new ContentData();
        contentData.add(
            Property.newLocalDate( "invoiceDate", LocalDateTime.ofInstant( invoice.invoiceDate, ZoneOffset.UTC ).toLocalDate() ) );
        contentData.add( Property.newString( "recipient", invoice.recipient ) );

        for ( InvoiceLine line : invoice.lines )
        {
            DataSet invoiceLine = DataSet.newDataSet().name( "invoiceLine" ).build();
            invoiceLine.add( Property.newString( "text", line.text ) );

            invoiceLine.add( new Property( "money", resolveValue( line.money ) ) );
            invoiceLine.add( myNewProperty( "money", line.money ) );

            contentData.add( invoiceLine );
        }

        // print out
        System.out.println( contentData.getProperty( "invoiceDate" ).getLocalDate() );
        System.out.println( contentData.getProperty( "recipient" ).getString() );
        for ( Data invoiceLine : contentData.getDataSet( "invoiceLine" ).getArray() )
        {
            DataSet invoiceLineDS = invoiceLine.toDataSet();
            System.out.println( invoiceLineDS.getProperty( "text" ).getString() + ": " + invoiceLineDS.getProperty( "money" ).getString() );
        }

    }

    @Test
    public void invoice_new_Type()
    {
        Invoice invoice = new Invoice();
        invoice.invoiceDate = Instant.now();
        invoice.recipient = "Runar Myklebust";
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 4mm", 120.00 ) );
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 10mm", 120.00 ) );

        ContentData contentData = new ContentData();
        contentData.add(
            Property.newLocalDate( "invoiceDate", LocalDateTime.ofInstant( invoice.invoiceDate, ZoneOffset.UTC ).toLocalDate() ) );
        contentData.add( Property.newString( "recipient", invoice.recipient ) );

        for ( InvoiceLine line : invoice.lines )
        {
            DataSet invoiceLine = DataSet.newDataSet( "invoiceLine" ).build();
            invoiceLine.add( Property.newString( "text", line.text ) );
            invoiceLine.add( myNewProperty( "money", line.money ) );
            contentData.add( invoiceLine );
        }

        // print out
        System.out.println( contentData.getProperty( "invoiceDate" ).getLocalDate() );
        System.out.println( contentData.getProperty( "recipient" ).getString() );
        for ( Data invoiceLine : contentData.getDataSet( "invoiceLine" ).getArray() )
        {
            DataSet invoiceLineDS = invoiceLine.toDataSet();
            System.out.println( invoiceLineDS.getProperty( "text" ).getString() + ": " + invoiceLineDS.getProperty( "money" ).getString() );
        }

    }

    @Test
    public void invoice_setData_with_Value_Type()
    {
        Invoice invoice = new Invoice();
        invoice.invoiceDate = Instant.now();
        invoice.recipient = "Runar Myklebust";
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 4mm", 120.00 ) );
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 10mm", 120.00 ) );

        ContentData contentData = new ContentData();
        contentData.setProperty( "invoiceDate",
                                 Value.newLocalDate( LocalDateTime.ofInstant( invoice.invoiceDate, ZoneOffset.UTC ).toLocalDate() ) );
        contentData.setProperty( "recipient", Value.newString( invoice.recipient ) );

        for ( InvoiceLine line : invoice.lines )
        {
            DataSet invoiceLine = new DataSet( "invoiceLine" );
            invoiceLine.setProperty( "text", Value.newString( line.text ) );
            invoiceLine.setProperty( "money", myNewValue( line.money ) );
            contentData.add( invoiceLine );
        }

        // print out
        System.out.println( contentData.getProperty( "invoiceDate" ).getLocalDate() );
        System.out.println( contentData.getProperty( "recipient" ).getString() );
        for ( Data invoiceLine : contentData.getDataSet( "invoiceLine" ).getArray() )
        {
            DataSet invoiceLineDS = invoiceLine.toDataSet();
            System.out.println( invoiceLineDS.getProperty( "text" ).getString() + ": " + invoiceLineDS.getProperty( "money" ).getString() );
        }

    }

    private Value resolveValue( final Object value )
    {
        if ( value instanceof Double )
        {
            return Value.newDouble( value );
        }
        else
        {
            return null;
        }
    }

    private Data myNewProperty( final String name, final Object value )
    {
        if ( value instanceof Double )
        {
            return Property.newDouble( name, value );
        }
        else
        {
            return null;
        }
    }

    private Value myNewValue( final Object value )
    {
        if ( value instanceof Double )
        {
            return Value.newDouble( ( value ) );
        }
        else
        {
            return null;
        }
    }


    private class Invoice
    {

        private Instant invoiceDate;

        private String recipient;

        private List<InvoiceLine> lines = new ArrayList<>();

    }

    private class InvoiceLine
    {
        private String text;

        private Double money;

        private InvoiceLine( final String text, final Double money )
        {
            this.text = text;
            this.money = money;
        }
    }


}
