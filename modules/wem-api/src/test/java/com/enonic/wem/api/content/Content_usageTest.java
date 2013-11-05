package com.enonic.wem.api.content;


import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

import static com.enonic.wem.api.data.DataSet.newDataSet;
import static com.enonic.wem.api.data.type.ValueTypes.DATE_MIDNIGHT;
import static com.enonic.wem.api.data.type.ValueTypes.DOUBLE;
import static com.enonic.wem.api.data.type.ValueTypes.HTML_PART;
import static com.enonic.wem.api.data.type.ValueTypes.LONG;
import static com.enonic.wem.api.data.type.ValueTypes.STRING;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class Content_usageTest
{
    @Test
    public void dataSet_setProperty()
    {
        DataSet dataSet = new ContentData();

        // exercise
        dataSet.setProperty( "myText", new Value.String( "abc" ) );
        dataSet.setProperty( "myNum", new Value.Long( 123 ) );
        dataSet.setProperty( "myDate", new Value.DateMidnight( new org.joda.time.DateMidnight( 2013, 1, 13 ) ) );
        dataSet.setProperty( "myDec", new Value.Double( 123.123 ) );
        dataSet.setProperty( "myHtml", new Value.HtmlPart( "<p>abc</p>" ) );

        // verify
        assertEquals( STRING, dataSet.getProperty( "myText" ).getValueType() );
        assertEquals( LONG, dataSet.getProperty( "myNum" ).getValueType() );
        assertEquals( DATE_MIDNIGHT, dataSet.getProperty( "myDate" ).getValueType() );
        assertEquals( DOUBLE, dataSet.getProperty( "myDec" ).getValueType() );
        assertEquals( HTML_PART, dataSet.getProperty( "myHtml" ).getValueType() );
    }

    @Test
    public void dataSet_setProperty_with_array_of_Values()
    {
        DataSet dataSet = new ContentData();

        // exercise
        dataSet.setProperty( "myText", new Value.String( "aaa" ), new Value.String( "bbb" ), new Value.String( "ccc" ) );

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
        dataSet.setProperty( "mySet.myText", new Value.String( "abc" ) );
        dataSet.setProperty( "mySet.myNum", new Value.Long( 123 ) );
        dataSet.setProperty( "mySet.myDate", new Value.DateMidnight( new org.joda.time.DateMidnight( 2013, 1, 13 ) ) );
        dataSet.setProperty( "mySet.myDec", new Value.Double( 123.123 ) );
        dataSet.setProperty( "mySet.myHtml", new Value.HtmlPart( "<p>abc</p>" ) );

        // verify
        assertEquals( "mySet.myText", dataSet.getProperty( "mySet.myText" ).getPath().toString() );
        assertEquals( "mySet.myNum", dataSet.getProperty( "mySet.myNum" ).getPath().toString() );
        assertEquals( "mySet.myDate", dataSet.getProperty( "mySet.myDate" ).getPath().toString() );
        assertEquals( "mySet.myDec", dataSet.getProperty( "mySet.myDec" ).getPath().toString() );
        assertEquals( "mySet.myHtml", dataSet.getProperty( "mySet.myHtml" ).getPath().toString() );
    }

    @Test
    public void dataSet_add_Data_using_newProperty()
    {
        DataSet dataSet = new ContentData();

        // exercise
        dataSet.add( new Property.String( "myText", "abc" ) );
        dataSet.add( new Property.Long( "myNum", 123L ) );
        dataSet.add( new Property.Double( "myDec", 123.123 ) );
        dataSet.add( new Property.Date( "myDate", new org.joda.time.DateMidnight( 2013, 1, 13 ) ) );
        dataSet.add( new Property.HtmlPart( "myHtml", "<p>abc</p>" ) );

        // verify
        assertEquals( STRING, dataSet.getProperty( "myText" ).getValueType() );
        assertEquals( LONG, dataSet.getProperty( "myNum" ).getValueType() );
        assertEquals( DATE_MIDNIGHT, dataSet.getProperty( "myDate" ).getValueType() );
        assertEquals( DOUBLE, dataSet.getProperty( "myDec" ).getValueType() );
        assertEquals( HTML_PART, dataSet.getProperty( "myHtml" ).getValueType() );

        assertTrue( dataSet.getProperty( "myText" ) instanceof Property.String );
        assertTrue( dataSet.getProperty( "myNum" ) instanceof Property.Long );
        assertTrue( dataSet.getProperty( "myDec" ) instanceof Property.Double );
        assertTrue( dataSet.getProperty( "myDate" ) instanceof Property.Date );
        assertTrue( dataSet.getProperty( "myHtml" ) instanceof Property.HtmlPart );
    }

    @Test
    public void dataSet_add_Data_using_newPropertyType()
    {
        DataSet dataSet = new ContentData();

        // exercise
        dataSet.add( new Property.String( "myText", "abc" ) );
        dataSet.add( new Property.Long( "myNum", 123L ) );
        dataSet.add( new Property.Double( "myDec", 123.123 ) );
        dataSet.add( new Property.Date( "myDate", new DateMidnight( 2013, 1, 13 ) ) );
        dataSet.add( new Property.HtmlPart( "myHtml", "<p>abc</p>" ) );

        // verify
        assertEquals( STRING, dataSet.getProperty( "myText" ).getValueType() );
        assertEquals( LONG, dataSet.getProperty( "myNum" ).getValueType() );
        assertEquals( DATE_MIDNIGHT, dataSet.getProperty( "myDate" ).getValueType() );
        assertEquals( DOUBLE, dataSet.getProperty( "myDec" ).getValueType() );
        assertEquals( HTML_PART, dataSet.getProperty( "myHtml" ).getValueType() );

        assertTrue( dataSet.getProperty( "myText" ) instanceof Property.String );
        assertTrue( dataSet.getProperty( "myNum" ) instanceof Property.Long );
        assertTrue( dataSet.getProperty( "myDec" ) instanceof Property.Double );
        assertTrue( dataSet.getProperty( "myDate" ) instanceof Property.Date );
        assertTrue( dataSet.getProperty( "myHtml" ) instanceof Property.HtmlPart );
    }

    @Test
    public void dataSet_add_Data_using_new_PropertyType()
    {
        DataSet dataSet = new ContentData();

        // exercise
        dataSet.add( new Property.String( "myText", "abc" ) );
        dataSet.add( new Property.Long( "myNum", 123L ) );
        dataSet.add( new Property.Double( "myDec", 123.123 ) );
        dataSet.add( new Property.Date( "myDate", new DateMidnight( 2013, 1, 13 ) ) );
        dataSet.add( new Property.HtmlPart( "myHtml", "<p>abc</p>" ) );

        // verify
        assertEquals( STRING, dataSet.getProperty( "myText" ).getValueType() );
        assertEquals( LONG, dataSet.getProperty( "myNum" ).getValueType() );
        assertEquals( DATE_MIDNIGHT, dataSet.getProperty( "myDate" ).getValueType() );
        assertEquals( DOUBLE, dataSet.getProperty( "myDec" ).getValueType() );
        assertEquals( HTML_PART, dataSet.getProperty( "myHtml" ).getValueType() );

        assertTrue( dataSet.getProperty( "myText" ) instanceof Property.String );
        assertTrue( dataSet.getProperty( "myNum" ) instanceof Property.Long );
        assertTrue( dataSet.getProperty( "myDec" ) instanceof Property.Double );
        assertTrue( dataSet.getProperty( "myDate" ) instanceof Property.Date );
        assertTrue( dataSet.getProperty( "myHtml" ) instanceof Property.HtmlPart );
    }

    @Test
    public void dataSet_addProperty()
    {
        DataSet dataSet = new ContentData();

        // exercise
        dataSet.addProperty( "myText", new Value.String( "abc" ) );
        dataSet.addProperty( "myNum", new Value.Long( 123 ) );
        dataSet.addProperty( "myDec", new Value.Double( 123.123 ) );
        dataSet.addProperty( "myDate", new Value.DateMidnight( new org.joda.time.DateMidnight( 2013, 1, 13 ) ) );
        dataSet.addProperty( "myHtml", new Value.HtmlPart( "<p>abc</p>" ) );

        // verify
        assertEquals( STRING, dataSet.getProperty( "myText" ).getValueType() );
        assertEquals( LONG, dataSet.getProperty( "myNum" ).getValueType() );
        assertEquals( DATE_MIDNIGHT, dataSet.getProperty( "myDate" ).getValueType() );
        assertEquals( DOUBLE, dataSet.getProperty( "myDec" ).getValueType() );
        assertEquals( HTML_PART, dataSet.getProperty( "myHtml" ).getValueType() );

        assertTrue( dataSet.getProperty( "myText" ) instanceof Property.String );
        assertTrue( dataSet.getProperty( "myNum" ) instanceof Property.Long );
        assertTrue( dataSet.getProperty( "myDec" ) instanceof Property.Double );
        assertTrue( dataSet.getProperty( "myDate" ) instanceof Property.Date );
        assertTrue( dataSet.getProperty( "myHtml" ) instanceof Property.HtmlPart );
    }

    @Test
    public void dataSet_addProperty_with_DataPath_containing_DataSet()
    {
        ContentData contentData = new ContentData();

        // exercise
        contentData.addProperty( "mySet.myText", new Value.String( "abc" ) );
        contentData.addProperty( "mySet.myNum", new Value.Long( 123 ) );
        contentData.addProperty( "mySet.myDec", new Value.Double( 123.123 ) );
        contentData.addProperty( "mySet.myDate", new Value.DateMidnight( new org.joda.time.DateMidnight( 2013, 1, 13 ) ) );
        contentData.addProperty( "mySet.myHtml", new Value.HtmlPart( "<p>abc</p>" ) );

        // verify
        assertEquals( "mySet.myText", contentData.getProperty( "mySet.myText" ).getPath().toString() );
        assertEquals( "mySet.myNum", contentData.getProperty( "mySet.myNum" ).getPath().toString() );
        assertEquals( "mySet.myDate", contentData.getProperty( "mySet.myDate" ).getPath().toString() );
        assertEquals( "mySet.myDec", contentData.getProperty( "mySet.myDec" ).getPath().toString() );
        assertEquals( "mySet.myHtml", contentData.getProperty( "mySet.myHtml" ).getPath().toString() );
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
        dataSet.add( new Property.String( "myText", "abc" ) );
        dataSet.add( new Property.Long( "myNum", 123L ) );
        dataSet.add( new Property.Double( "myDec", 123.123 ) );
        dataSet.add( new Property.Date( "myDate", new DateMidnight( 2013, 1, 13 ) ) );
        dataSet.add( new Property.HtmlPart( "myHtml", "<p>abc</p>" ) );

        // exercise & verify
        assertEquals( "abc", dataSet.getProperty( "myText" ).getString() );
        assertEquals( new Long( 123L ), dataSet.getProperty( "myNum" ).getLong() );
        assertEquals( 123.123, dataSet.getProperty( "myDec" ).getDouble() );
        assertEquals( new org.joda.time.DateMidnight( 2013, 1, 13 ), dataSet.getProperty( "myDate" ).getDateMidnight() );
        assertEquals( "<p>abc</p>", dataSet.getProperty( "myHtml" ).getString() );
    }

    @Test
    public void dataSet_add_Data_array()
    {
        DataSet dataSet = new ContentData();

        // exercise
        dataSet.add( new Property.String( "myText", "a" ) );
        dataSet.add( new Property.String( "myText", "b" ) );
        dataSet.add( new Property.String( "myText", "c" ) );

        // verify
        assertEquals( "a", dataSet.getProperty( "myText" ).getString( 0 ) );
        assertEquals( "b", dataSet.getProperty( "myText" ).getString( 1 ) );
        assertEquals( "c", dataSet.getProperty( "myText" ).getString( 2 ) );
    }

    @Test
    public void invoice_newProperty()
    {
        Invoice invoice = new Invoice();
        invoice.invoiceDate = DateTime.now();
        invoice.recipient = "Runar Myklebust";
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 4mm", 120.00 ) );
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 10mm", 120.00 ) );

        ContentData contentData = new ContentData();
        contentData.add( new Property.Date( "invoiceDate", invoice.invoiceDate.toDateMidnight() ) );
        contentData.add( new Property.String( "recipient", invoice.recipient ) );

        for ( InvoiceLine line : invoice.lines )
        {
            DataSet invoiceLine = DataSet.newDataSet().name( "invoiceLine" ).build();
            invoiceLine.add( new Property.String( "text", line.text ) );
            invoiceLine.add( new Property( "money", resolveValue( line.money ) ) );

            contentData.add( invoiceLine );
        }

        // print out
        System.out.println( contentData.getProperty( "invoiceDate" ).getDateMidnight() );
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
        invoice.invoiceDate = DateTime.now();
        invoice.recipient = "Runar Myklebust";
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 4mm", 120.00 ) );
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 10mm", 120.00 ) );

        ContentData contentData = new ContentData();
        contentData.add( new Property.Date( "invoiceDate", invoice.invoiceDate.toDateMidnight() ) );
        contentData.add( new Property.String( "recipient", invoice.recipient ) );

        for ( InvoiceLine line : invoice.lines )
        {
            DataSet invoiceLine = DataSet.newDataSet().name( "invoiceLine" ).build();
            invoiceLine.add( new Property.String( "text", line.text ) );

            invoiceLine.add( new Property( "money", resolveValue( line.money ) ) );
            invoiceLine.add( myNewProperty( "money", line.money ) );

            contentData.add( invoiceLine );
        }

        // print out
        System.out.println( contentData.getProperty( "invoiceDate" ).getDateMidnight() );
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
        invoice.invoiceDate = DateTime.now();
        invoice.recipient = "Runar Myklebust";
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 4mm", 120.00 ) );
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 10mm", 120.00 ) );

        ContentData contentData = new ContentData();
        contentData.add( new Property.Date( "invoiceDate", invoice.invoiceDate.toDateMidnight() ) );
        contentData.add( new Property.String( "recipient", invoice.recipient ) );

        for ( InvoiceLine line : invoice.lines )
        {
            DataSet invoiceLine = DataSet.newDataSet( "invoiceLine" ).build();
            invoiceLine.add( new Property.String( "text", line.text ) );
            invoiceLine.add( myNewProperty( "money", line.money ) );
            contentData.add( invoiceLine );
        }

        // print out
        System.out.println( contentData.getProperty( "invoiceDate" ).getDateMidnight() );
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
        invoice.invoiceDate = DateTime.now();
        invoice.recipient = "Runar Myklebust";
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 4mm", 120.00 ) );
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 10mm", 120.00 ) );

        ContentData contentData = new ContentData();
        contentData.setProperty( "invoiceDate", new Value.DateMidnight( invoice.invoiceDate.toDateMidnight() ) );
        contentData.setProperty( "recipient", new Value.String( invoice.recipient ) );

        for ( InvoiceLine line : invoice.lines )
        {
            DataSet invoiceLine = new DataSet( "invoiceLine" );
            invoiceLine.setProperty( "text", new Value.String( line.text ) );
            invoiceLine.setProperty( "money", myNewValue( line.money ) );
            contentData.add( invoiceLine );
        }

        // print out
        System.out.println( contentData.getProperty( "invoiceDate" ).getDateMidnight() );
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
            return new Value.Double( (Double) value );
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
            return new Property.Double( name, (Double) value );
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
            return new Value.Double( ( (Double) value ) );
        }
        else
        {
            return null;
        }
    }


    private class Invoice
    {

        private DateTime invoiceDate;

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
