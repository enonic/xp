package com.enonic.wem.api.content;


import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Entry;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.data.type.BaseDataType;
import com.enonic.wem.api.content.data.type.DataTypes;

import static com.enonic.wem.api.content.data.Data.Date.newDate;
import static com.enonic.wem.api.content.data.Data.DecimalNumber.newDecimalNumber;
import static com.enonic.wem.api.content.data.Data.HtmlPart.newHtmlPart;
import static com.enonic.wem.api.content.data.Data.Text.newText;
import static com.enonic.wem.api.content.data.Data.WholeNumber.newWholeNumber;
import static com.enonic.wem.api.content.data.Data.newData;
import static com.enonic.wem.api.content.data.DataSet.newDataSet;
import static com.enonic.wem.api.content.data.Value.newValue;
import static com.enonic.wem.api.content.data.type.DataTypes.DATE_MIDNIGHT;
import static com.enonic.wem.api.content.data.type.DataTypes.DECIMAL_NUMBER;
import static com.enonic.wem.api.content.data.type.DataTypes.HTML_PART;
import static com.enonic.wem.api.content.data.type.DataTypes.TEXT;
import static com.enonic.wem.api.content.data.type.DataTypes.WHOLE_NUMBER;
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
        dataSet.setData( "myText", new Value.Text( "abc" ) );
        dataSet.setData( "myNum", new Value.WholeNumber( 123 ) );
        dataSet.setData( "myDate", new Value.Date( new DateMidnight( 2013, 1, 13 ) ) );
        dataSet.setData( "myDec", new Value.DecimalNumber( 123.123 ) );
        dataSet.setData( "myHtml", new Value.HtmlPart( "<p>abc</p>" ) );

        // verify
        assertEquals( TEXT, dataSet.getData( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, dataSet.getData( "myNum" ).getType() );
        assertEquals( DATE_MIDNIGHT, dataSet.getData( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, dataSet.getData( "myDec" ).getType() );
        assertEquals( HTML_PART, dataSet.getData( "myHtml" ).getType() );
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
        assertEquals( TEXT, dataSet.getData( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, dataSet.getData( "myNum" ).getType() );
        assertEquals( DATE_MIDNIGHT, dataSet.getData( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, dataSet.getData( "myDec" ).getType() );
        assertEquals( HTML_PART, dataSet.getData( "myHtml" ).getType() );

        assertFalse( dataSet.getData( "myText" ) instanceof Data.Text );
        assertFalse( dataSet.getData( "myNum" ) instanceof Data.WholeNumber );
        assertFalse( dataSet.getData( "myDec" ) instanceof Data.DecimalNumber );
        assertFalse( dataSet.getData( "myDate" ) instanceof Data.Date );
        assertFalse( dataSet.getData( "myHtml" ) instanceof Data.HtmlPart );
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
        assertEquals( TEXT, dataSet.getData( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, dataSet.getData( "myNum" ).getType() );
        assertEquals( DATE_MIDNIGHT, dataSet.getData( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, dataSet.getData( "myDec" ).getType() );
        assertEquals( HTML_PART, dataSet.getData( "myHtml" ).getType() );

        assertTrue( dataSet.getData( "myText" ) instanceof Data.Text );
        assertTrue( dataSet.getData( "myNum" ) instanceof Data.WholeNumber );
        assertTrue( dataSet.getData( "myDec" ) instanceof Data.DecimalNumber );
        assertTrue( dataSet.getData( "myDate" ) instanceof Data.Date );
        assertTrue( dataSet.getData( "myHtml" ) instanceof Data.HtmlPart );
    }

    @Test
    public void dataSet_add_Data_using_new_DataType()
    {
        DataSet dataSet = new RootDataSet();

        // exercise
        dataSet.add( new Data.Text( "myText", "abc" ) );
        dataSet.add( new Data.WholeNumber( "myNum", 123L ) );
        dataSet.add( new Data.DecimalNumber( "myDec", 123.123 ) );
        dataSet.add( new Data.Date( "myDate", new DateMidnight( 2013, 1, 13 ) ) );
        dataSet.add( new Data.HtmlPart( "myHtml", "<p>abc</p>" ) );

        // verify
        assertEquals( TEXT, dataSet.getData( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, dataSet.getData( "myNum" ).getType() );
        assertEquals( DATE_MIDNIGHT, dataSet.getData( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, dataSet.getData( "myDec" ).getType() );
        assertEquals( HTML_PART, dataSet.getData( "myHtml" ).getType() );

        assertTrue( dataSet.getData( "myText" ) instanceof Data.Text );
        assertTrue( dataSet.getData( "myNum" ) instanceof Data.WholeNumber );
        assertTrue( dataSet.getData( "myDec" ) instanceof Data.DecimalNumber );
        assertTrue( dataSet.getData( "myDate" ) instanceof Data.Date );
        assertTrue( dataSet.getData( "myHtml" ) instanceof Data.HtmlPart );
    }

    @Test
    public void dataSet_add_Data_using_new_ValueType()
    {
        DataSet dataSet = new RootDataSet();

        // exercise
        dataSet.addData( "myText", new Value.Text( "abc" ) );
        dataSet.addData( "myNum", new Value.WholeNumber( 123 ) );
        dataSet.addData( "myDec", new Value.DecimalNumber( 123.123 ) );
        dataSet.addData( "myDate", new Value.Date( new DateMidnight( 2013, 1, 13 ) ) );
        dataSet.addData( "myHtml", new Value.HtmlPart( "<p>abc</p>" ) );

        // verify
        assertEquals( TEXT, dataSet.getData( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, dataSet.getData( "myNum" ).getType() );
        assertEquals( DATE_MIDNIGHT, dataSet.getData( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, dataSet.getData( "myDec" ).getType() );
        assertEquals( HTML_PART, dataSet.getData( "myHtml" ).getType() );

        assertTrue( dataSet.getData( "myText" ) instanceof Data.Text );
        assertTrue( dataSet.getData( "myNum" ) instanceof Data.WholeNumber );
        assertTrue( dataSet.getData( "myDec" ) instanceof Data.DecimalNumber );
        assertTrue( dataSet.getData( "myDate" ) instanceof Data.Date );
        assertTrue( dataSet.getData( "myHtml" ) instanceof Data.HtmlPart );
    }

    @Test
    public void dataSet_add_Data_using_newValue()
    {
        DataSet dataSet = new RootDataSet();

        // exercise
        dataSet.addData( "myText", newValue().type( DataTypes.TEXT ).value( "abc" ) );
        dataSet.addData( "myNum", newValue().type( DataTypes.WHOLE_NUMBER ).value( 123L ) );
        dataSet.addData( "myDec", newValue().type( DataTypes.DECIMAL_NUMBER ).value( 123.123 ) );
        dataSet.addData( "myDate", newValue().type( DataTypes.DATE_MIDNIGHT ).value( new DateMidnight( 2013, 1, 13 ) ) );
        dataSet.addData( "myHtml", newValue().type( DataTypes.HTML_PART ).value( "<p>abc</p>" ) );

        // verify
        assertEquals( TEXT, dataSet.getData( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, dataSet.getData( "myNum" ).getType() );
        assertEquals( DATE_MIDNIGHT, dataSet.getData( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, dataSet.getData( "myDec" ).getType() );
        assertEquals( HTML_PART, dataSet.getData( "myHtml" ).getType() );

        assertTrue( dataSet.getData( "myText" ) instanceof Data.Text );
        assertTrue( dataSet.getData( "myNum" ) instanceof Data.WholeNumber );
        assertTrue( dataSet.getData( "myDec" ) instanceof Data.DecimalNumber );
        assertTrue( dataSet.getData( "myDate" ) instanceof Data.Date );
        assertTrue( dataSet.getData( "myHtml" ) instanceof Data.HtmlPart );
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
        assertEquals( "abc", dataSet.getData( "myText" ).getString() );
        assertEquals( new Long( 123L ), dataSet.getData( "myNum" ).getLong() );
        assertEquals( 123.123, dataSet.getData( "myDec" ).getDouble() );
        assertEquals( new DateMidnight( 2013, 1, 13 ), dataSet.getData( "myDate" ).getDate() );
        assertEquals( "<p>abc</p>", dataSet.getData( "myHtml" ).getString() );
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
        assertEquals( "a", dataSet.getData( "myText" ).getString( 0 ) );
        assertEquals( "b", dataSet.getData( "myText" ).getString( 1 ) );
        assertEquals( "c", dataSet.getData( "myText" ).getString( 2 ) );
    }

    @Test
    @Ignore
    public void dataSet_setData_array()
    {
        DataSet dataSet = new RootDataSet();

        // exercise
        //TODO dataSet.setData( "myText", "a", "b", "c" );

        // verify
        assertEquals( "a", dataSet.getData( "myText" ).getString( 0 ) );
        assertEquals( "b", dataSet.getData( "myText" ).getString( 1 ) );
        assertEquals( "c", dataSet.getData( "myText" ).getString( 2 ) );
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
        rootDataSet.add( newData( "invoiceDate" ).type( DataTypes.DATE_MIDNIGHT ).value( invoice.invoiceDate.toDateMidnight() ).build() );
        rootDataSet.add( newData( "recipient" ).type( DataTypes.TEXT ).value( invoice.recipient ).build() );

        for ( InvoiceLine line : invoice.lines )
        {
            DataSet invoiceLine = DataSet.newDataSet().name( "invoiceLine" ).build();
            invoiceLine.add( newData( "text" ).type( DataTypes.TEXT ).value( line.text ).build() );
            invoiceLine.add( newData( "money" ).type( resolveType( line.money ) ).value( line.money ).build() );

            rootDataSet.add( invoiceLine );
        }

        // print out
        System.out.println( rootDataSet.getData( "invoiceDate" ).getDate() );
        System.out.println( rootDataSet.getData( "recipient" ).getString() );
        for ( Entry invoiceLine : rootDataSet.getDataSet( "invoiceLine" ).getArray() )
        {
            DataSet invoiceLineDS = invoiceLine.toDataSet();
            System.out.println( invoiceLineDS.getData( "text" ).getString() + ": " + invoiceLineDS.getData( "money" ).getString() );
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
        System.out.println( rootDataSet.getData( "invoiceDate" ).getDate() );
        System.out.println( rootDataSet.getData( "recipient" ).getString() );
        for ( Entry invoiceLine : rootDataSet.getDataSet( "invoiceLine" ).getArray() )
        {
            DataSet invoiceLineDS = invoiceLine.toDataSet();
            System.out.println( invoiceLineDS.getData( "text" ).getString() + ": " + invoiceLineDS.getData( "money" ).getString() );
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
        rootDataSet.add( new Data.Date( "invoiceDate", invoice.invoiceDate.toDateMidnight() ) );
        rootDataSet.add( new Data.Text( "recipient", invoice.recipient ) );

        for ( InvoiceLine line : invoice.lines )
        {
            DataSet invoiceLine = DataSet.newDataSet().name( "invoiceLine" ).build();
            invoiceLine.add( new Data.Text( "text", line.text ) );
            invoiceLine.add( myNewData( "money", line.money ) );
            rootDataSet.add( invoiceLine );
        }

        // print out
        System.out.println( rootDataSet.getData( "invoiceDate" ).getDate() );
        System.out.println( rootDataSet.getData( "recipient" ).getString() );
        for ( Entry invoiceLine : rootDataSet.getDataSet( "invoiceLine" ).getArray() )
        {
            DataSet invoiceLineDS = invoiceLine.toDataSet();
            System.out.println( invoiceLineDS.getData( "text" ).getString() + ": " + invoiceLineDS.getData( "money" ).getString() );
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
        rootDataSet.setData( "invoiceDate", new Value.Date( invoice.invoiceDate.toDateMidnight() ) );
        rootDataSet.setData( "recipient", new Value.Text( invoice.recipient ) );

        for ( InvoiceLine line : invoice.lines )
        {
            DataSet invoiceLine = DataSet.newDataSet().name( "invoiceLine" ).build();
            invoiceLine.setData( "text", new Value.Text( line.text ) );
            invoiceLine.setData( "money", myNewValue( line.money ) );
            rootDataSet.add( invoiceLine );
        }

        // print out
        System.out.println( rootDataSet.getData( "invoiceDate" ).getDate() );
        System.out.println( rootDataSet.getData( "recipient" ).getString() );
        for ( Entry invoiceLine : rootDataSet.getDataSet( "invoiceLine" ).getArray() )
        {
            DataSet invoiceLineDS = invoiceLine.toDataSet();
            System.out.println( invoiceLineDS.getData( "text" ).getString() + ": " + invoiceLineDS.getData( "money" ).getString() );
        }

    }

    private BaseDataType resolveType( final Object value )
    {
        if ( value instanceof Double )
        {
            return DataTypes.DECIMAL_NUMBER;
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
