package com.enonic.wem.api.content;


import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Entry;
import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.data.type.BasePropertyType;
import com.enonic.wem.api.content.data.type.PropertyTypes;

import static com.enonic.wem.api.content.data.DataSet.newDataSet;
import static com.enonic.wem.api.content.data.Property.Date.newDate;
import static com.enonic.wem.api.content.data.Property.DecimalNumber.newDecimalNumber;
import static com.enonic.wem.api.content.data.Property.HtmlPart.newHtmlPart;
import static com.enonic.wem.api.content.data.Property.Text.newText;
import static com.enonic.wem.api.content.data.Property.WholeNumber.newWholeNumber;
import static com.enonic.wem.api.content.data.Property.newData;
import static com.enonic.wem.api.content.data.Value.newValue;
import static com.enonic.wem.api.content.data.type.PropertyTypes.DATE_MIDNIGHT;
import static com.enonic.wem.api.content.data.type.PropertyTypes.DECIMAL_NUMBER;
import static com.enonic.wem.api.content.data.type.PropertyTypes.HTML_PART;
import static com.enonic.wem.api.content.data.type.PropertyTypes.TEXT;
import static com.enonic.wem.api.content.data.type.PropertyTypes.WHOLE_NUMBER;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class Content_usageTest
{
    @Test
    public void dataSet_setData()
    {
        DataSet dataSet = new RootDataSet();

        // exercise
        dataSet.setProperty( "myText", new Value.Text( "abc" ) );
        dataSet.setProperty( "myNum", new Value.WholeNumber( 123 ) );
        dataSet.setProperty( "myDate", new Value.Date( new DateMidnight( 2013, 1, 13 ) ) );
        dataSet.setProperty( "myDec", new Value.DecimalNumber( 123.123 ) );
        dataSet.setProperty( "myHtml", new Value.HtmlPart( "<p>abc</p>" ) );

        // verify
        assertEquals( TEXT, dataSet.getProperty( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, dataSet.getProperty( "myNum" ).getType() );
        assertEquals( DATE_MIDNIGHT, dataSet.getProperty( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, dataSet.getProperty( "myDec" ).getType() );
        assertEquals( HTML_PART, dataSet.getProperty( "myHtml" ).getType() );
    }

    @Test
    public void dataSet_add_Data_using_newData()
    {
        DataSet dataSet = new RootDataSet();

        // exercise
        dataSet.add( newData( "myText" ).type( TEXT ).value( "abc" ).build() );
        dataSet.add( newData( "myNum" ).type( WHOLE_NUMBER ).value( 123L ).build() );
        dataSet.add( newData( "myDec" ).type( DECIMAL_NUMBER ).value( 123.123 ).build() );
        dataSet.add( newData( "myDate" ).type( DATE_MIDNIGHT ).value( new DateMidnight( 2013, 1, 13 ) ).build() );
        dataSet.add( newData( "myHtml" ).type( HTML_PART ).value( "<p>abc</p>" ).build() );

        // verify
        assertEquals( TEXT, dataSet.getProperty( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, dataSet.getProperty( "myNum" ).getType() );
        assertEquals( DATE_MIDNIGHT, dataSet.getProperty( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, dataSet.getProperty( "myDec" ).getType() );
        assertEquals( HTML_PART, dataSet.getProperty( "myHtml" ).getType() );

        assertFalse( dataSet.getProperty( "myText" ) instanceof Property.Text );
        assertFalse( dataSet.getProperty( "myNum" ) instanceof Property.WholeNumber );
        assertFalse( dataSet.getProperty( "myDec" ) instanceof Property.DecimalNumber );
        assertFalse( dataSet.getProperty( "myDate" ) instanceof Property.Date );
        assertFalse( dataSet.getProperty( "myHtml" ) instanceof Property.HtmlPart );
    }

    @Test
    public void dataSet_add_Data_using_newDataType()
    {
        DataSet dataSet = new RootDataSet();

        // exercise
        dataSet.add( newText( "myText" ).value( "abc" ) );
        dataSet.add( newWholeNumber( "myNum" ).value( 123L ) );
        dataSet.add( newDecimalNumber( "myDec" ).value( 123.123 ) );
        dataSet.add( newDate( "myDate" ).value( new DateMidnight( 2013, 1, 13 ) ) );
        dataSet.add( newHtmlPart( "myHtml" ).value( "<p>abc</p>" ) );

        // verify
        assertEquals( TEXT, dataSet.getProperty( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, dataSet.getProperty( "myNum" ).getType() );
        assertEquals( DATE_MIDNIGHT, dataSet.getProperty( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, dataSet.getProperty( "myDec" ).getType() );
        assertEquals( HTML_PART, dataSet.getProperty( "myHtml" ).getType() );

        assertTrue( dataSet.getProperty( "myText" ) instanceof Property.Text );
        assertTrue( dataSet.getProperty( "myNum" ) instanceof Property.WholeNumber );
        assertTrue( dataSet.getProperty( "myDec" ) instanceof Property.DecimalNumber );
        assertTrue( dataSet.getProperty( "myDate" ) instanceof Property.Date );
        assertTrue( dataSet.getProperty( "myHtml" ) instanceof Property.HtmlPart );
    }

    @Test
    public void dataSet_add_Data_using_new_DataType()
    {
        DataSet dataSet = new RootDataSet();

        // exercise
        dataSet.add( new Property.Text( "myText", "abc" ) );
        dataSet.add( new Property.WholeNumber( "myNum", 123L ) );
        dataSet.add( new Property.DecimalNumber( "myDec", 123.123 ) );
        dataSet.add( new Property.Date( "myDate", new DateMidnight( 2013, 1, 13 ) ) );
        dataSet.add( new Property.HtmlPart( "myHtml", "<p>abc</p>" ) );

        // verify
        assertEquals( TEXT, dataSet.getProperty( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, dataSet.getProperty( "myNum" ).getType() );
        assertEquals( DATE_MIDNIGHT, dataSet.getProperty( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, dataSet.getProperty( "myDec" ).getType() );
        assertEquals( HTML_PART, dataSet.getProperty( "myHtml" ).getType() );

        assertTrue( dataSet.getProperty( "myText" ) instanceof Property.Text );
        assertTrue( dataSet.getProperty( "myNum" ) instanceof Property.WholeNumber );
        assertTrue( dataSet.getProperty( "myDec" ) instanceof Property.DecimalNumber );
        assertTrue( dataSet.getProperty( "myDate" ) instanceof Property.Date );
        assertTrue( dataSet.getProperty( "myHtml" ) instanceof Property.HtmlPart );
    }

    @Test
    public void dataSet_add_Data_using_new_ValueType()
    {
        DataSet dataSet = new RootDataSet();

        // exercise
        dataSet.addProperty( "myText", new Value.Text( "abc" ) );
        dataSet.addProperty( "myNum", new Value.WholeNumber( 123 ) );
        dataSet.addProperty( "myDec", new Value.DecimalNumber( 123.123 ) );
        dataSet.addProperty( "myDate", new Value.Date( new DateMidnight( 2013, 1, 13 ) ) );
        dataSet.addProperty( "myHtml", new Value.HtmlPart( "<p>abc</p>" ) );

        // verify
        assertEquals( TEXT, dataSet.getProperty( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, dataSet.getProperty( "myNum" ).getType() );
        assertEquals( DATE_MIDNIGHT, dataSet.getProperty( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, dataSet.getProperty( "myDec" ).getType() );
        assertEquals( HTML_PART, dataSet.getProperty( "myHtml" ).getType() );

        assertTrue( dataSet.getProperty( "myText" ) instanceof Property.Text );
        assertTrue( dataSet.getProperty( "myNum" ) instanceof Property.WholeNumber );
        assertTrue( dataSet.getProperty( "myDec" ) instanceof Property.DecimalNumber );
        assertTrue( dataSet.getProperty( "myDate" ) instanceof Property.Date );
        assertTrue( dataSet.getProperty( "myHtml" ) instanceof Property.HtmlPart );
    }

    @Test
    public void dataSet_add_Data_using_newValue()
    {
        DataSet dataSet = new RootDataSet();

        // exercise
        dataSet.addProperty( "myText", newValue().type( PropertyTypes.TEXT ).value( "abc" ) );
        dataSet.addProperty( "myNum", newValue().type( PropertyTypes.WHOLE_NUMBER ).value( 123L ) );
        dataSet.addProperty( "myDec", newValue().type( PropertyTypes.DECIMAL_NUMBER ).value( 123.123 ) );
        dataSet.addProperty( "myDate", newValue().type( PropertyTypes.DATE_MIDNIGHT ).value( new DateMidnight( 2013, 1, 13 ) ) );
        dataSet.addProperty( "myHtml", newValue().type( PropertyTypes.HTML_PART ).value( "<p>abc</p>" ) );

        // verify
        assertEquals( TEXT, dataSet.getProperty( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, dataSet.getProperty( "myNum" ).getType() );
        assertEquals( DATE_MIDNIGHT, dataSet.getProperty( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, dataSet.getProperty( "myDec" ).getType() );
        assertEquals( HTML_PART, dataSet.getProperty( "myHtml" ).getType() );

        assertTrue( dataSet.getProperty( "myText" ) instanceof Property.Text );
        assertTrue( dataSet.getProperty( "myNum" ) instanceof Property.WholeNumber );
        assertTrue( dataSet.getProperty( "myDec" ) instanceof Property.DecimalNumber );
        assertTrue( dataSet.getProperty( "myDate" ) instanceof Property.Date );
        assertTrue( dataSet.getProperty( "myHtml" ) instanceof Property.HtmlPart );
    }

    @Test
    public void dataSet_add_DataSet()
    {
        DataSet parentDataSet = DataSet.newRootDataSet();

        // exercise
        parentDataSet.add( newDataSet().name( "mySet" ).build() );

        // verify
        assertNotNull( parentDataSet.getDataSet( "mySet" ) );
    }

    @Test
    public void data_getValue()
    {
        DataSet dataSet = new RootDataSet();
        dataSet.add( newData( "myText" ).type( TEXT ).value( "abc" ).build() );
        dataSet.add( newData( "myNum" ).type( WHOLE_NUMBER ).value( 123L ).build() );
        dataSet.add( newData( "myDec" ).type( DECIMAL_NUMBER ).value( 123.123 ).build() );
        dataSet.add( newData( "myDate" ).type( DATE_MIDNIGHT ).value( new DateMidnight( 2013, 1, 13 ) ).build() );
        dataSet.add( newData( "myHtml" ).type( HTML_PART ).value( "<p>abc</p>" ).build() );

        // exercise & verify
        assertEquals( "abc", dataSet.getProperty( "myText" ).getString() );
        assertEquals( new Long( 123L ), dataSet.getProperty( "myNum" ).getLong() );
        assertEquals( 123.123, dataSet.getProperty( "myDec" ).getDouble() );
        assertEquals( new DateMidnight( 2013, 1, 13 ), dataSet.getProperty( "myDate" ).getDate() );
        assertEquals( "<p>abc</p>", dataSet.getProperty( "myHtml" ).getString() );
    }

    @Test
    public void dataSet_add_Data_array()
    {
        DataSet dataSet = new RootDataSet();

        // exercise
        dataSet.add( newData( "myText" ).type( TEXT ).value( "a" ).build() );
        dataSet.add( newData( "myText" ).type( TEXT ).value( "b" ).build() );
        dataSet.add( newData( "myText" ).type( TEXT ).value( "c" ).build() );

        // verify
        assertEquals( "a", dataSet.getProperty( "myText" ).getString( 0 ) );
        assertEquals( "b", dataSet.getProperty( "myText" ).getString( 1 ) );
        assertEquals( "c", dataSet.getProperty( "myText" ).getString( 2 ) );
    }

    @Test
    @Ignore
    public void dataSet_setData_array()
    {
        DataSet dataSet = new RootDataSet();

        // exercise
        //TODO dataSet.setData( "myText", "a", "b", "c" );

        // verify
        assertEquals( "a", dataSet.getProperty( "myText" ).getString( 0 ) );
        assertEquals( "b", dataSet.getProperty( "myText" ).getString( 1 ) );
        assertEquals( "c", dataSet.getProperty( "myText" ).getString( 2 ) );
    }

    @Test
    public void invoice_newData()
    {
        Invoice invoice = new Invoice();
        invoice.invoiceDate = DateTime.now();
        invoice.recipient = "Runar Myklebust";
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 4mm", 120.00 ) );
        invoice.lines.add( new InvoiceLine( "1x1m Oak veneer, 10mm", 120.00 ) );

        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.add(
            newData( "invoiceDate" ).type( PropertyTypes.DATE_MIDNIGHT ).value( invoice.invoiceDate.toDateMidnight() ).build() );
        rootDataSet.add( newData( "recipient" ).type( PropertyTypes.TEXT ).value( invoice.recipient ).build() );

        for ( InvoiceLine line : invoice.lines )
        {
            DataSet invoiceLine = DataSet.newDataSet().name( "invoiceLine" ).build();
            invoiceLine.add( newData( "text" ).type( PropertyTypes.TEXT ).value( line.text ).build() );
            invoiceLine.add( newData( "money" ).type( resolveType( line.money ) ).value( line.money ).build() );

            rootDataSet.add( invoiceLine );
        }

        // print out
        System.out.println( rootDataSet.getProperty( "invoiceDate" ).getDate() );
        System.out.println( rootDataSet.getProperty( "recipient" ).getString() );
        for ( Entry invoiceLine : rootDataSet.getDataSet( "invoiceLine" ).getArray() )
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

        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.add( newDate( "invoiceDate" ).value( invoice.invoiceDate.toDateMidnight() ) );
        rootDataSet.add( newText( "recipient" ).value( invoice.recipient ) );

        for ( InvoiceLine line : invoice.lines )
        {
            DataSet invoiceLine = DataSet.newDataSet().name( "invoiceLine" ).build();
            invoiceLine.add( newText().name( "text" ).value( line.text ).build() );

            invoiceLine.add( newData( "money" ).type( resolveType( line.money ) ).value( line.money ).build() );
            invoiceLine.add( myNewData( "money", line.money ) );

            rootDataSet.add( invoiceLine );
        }

        // print out
        System.out.println( rootDataSet.getProperty( "invoiceDate" ).getDate() );
        System.out.println( rootDataSet.getProperty( "recipient" ).getString() );
        for ( Entry invoiceLine : rootDataSet.getDataSet( "invoiceLine" ).getArray() )
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

        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.add( new Property.Date( "invoiceDate", invoice.invoiceDate.toDateMidnight() ) );
        rootDataSet.add( new Property.Text( "recipient", invoice.recipient ) );

        for ( InvoiceLine line : invoice.lines )
        {
            DataSet invoiceLine = DataSet.newDataSet().name( "invoiceLine" ).build();
            invoiceLine.add( new Property.Text( "text", line.text ) );
            invoiceLine.add( myNewData( "money", line.money ) );
            rootDataSet.add( invoiceLine );
        }

        // print out
        System.out.println( rootDataSet.getProperty( "invoiceDate" ).getDate() );
        System.out.println( rootDataSet.getProperty( "recipient" ).getString() );
        for ( Entry invoiceLine : rootDataSet.getDataSet( "invoiceLine" ).getArray() )
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

        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "invoiceDate", new Value.Date( invoice.invoiceDate.toDateMidnight() ) );
        rootDataSet.setProperty( "recipient", new Value.Text( invoice.recipient ) );

        for ( InvoiceLine line : invoice.lines )
        {
            DataSet invoiceLine = DataSet.newDataSet().name( "invoiceLine" ).build();
            invoiceLine.setProperty( "text", new Value.Text( line.text ) );
            invoiceLine.setProperty( "money", myNewValue( line.money ) );
            rootDataSet.add( invoiceLine );
        }

        // print out
        System.out.println( rootDataSet.getProperty( "invoiceDate" ).getDate() );
        System.out.println( rootDataSet.getProperty( "recipient" ).getString() );
        for ( Entry invoiceLine : rootDataSet.getDataSet( "invoiceLine" ).getArray() )
        {
            DataSet invoiceLineDS = invoiceLine.toDataSet();
            System.out.println( invoiceLineDS.getProperty( "text" ).getString() + ": " + invoiceLineDS.getProperty( "money" ).getString() );
        }

    }

    private BasePropertyType resolveType( final Object value )
    {
        if ( value instanceof Double )
        {
            return PropertyTypes.DECIMAL_NUMBER;
        }
        else
        {
            return null;
        }
    }

    private Entry myNewData( final String text, final Object value )
    {
        if ( value instanceof Double )
        {
            return newDecimalNumber().name( text ).value( (Double) value ).build();
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
            return new Value.DecimalNumber( ( (Double) value ) );
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
