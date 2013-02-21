package com.enonic.wem.api.content;


import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Date;
import com.enonic.wem.api.content.data.DecimalNumber;
import com.enonic.wem.api.content.data.HtmlPart;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.Text;
import com.enonic.wem.api.content.data.WholeNumber;

import static com.enonic.wem.api.content.data.Data.newData;
import static com.enonic.wem.api.content.data.Data.newDate;
import static com.enonic.wem.api.content.data.Data.newDecimalNumber;
import static com.enonic.wem.api.content.data.Data.newHtmlPart;
import static com.enonic.wem.api.content.data.Data.newText;
import static com.enonic.wem.api.content.data.Data.newWholeNumber;
import static com.enonic.wem.api.content.data.DataSet.newDataSet;
import static com.enonic.wem.api.content.data.type.DataTypes.DATE;
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
        dataSet.setData( "myText", "abc" );
        dataSet.setData( "myNum", 123L );
        dataSet.setData( "myDate", new DateMidnight( 2013, 1, 13 ) );
        dataSet.setData( "myDec", 123.123 );
        dataSet.setData( "myHtml", HTML_PART, "<p>abc</p>" );

        // verify
        assertEquals( TEXT, dataSet.getData( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, dataSet.getData( "myNum" ).getType() );
        assertEquals( DATE, dataSet.getData( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, dataSet.getData( "myDec" ).getType() );
        assertEquals( HTML_PART, dataSet.getData( "myHtml" ).getType() );
    }

    @Test
    public void dataSet_add_Data_using_newData()
    {
        DataSet dataSet = new RootDataSet();

        // exercise
        dataSet.add( newData().name( "myText" ).value( "abc" ).type( TEXT ).build() );
        dataSet.add( newData().name( "myNum" ).value( 123L ).type( WHOLE_NUMBER ).build() );
        dataSet.add( newData().name( "myDec" ).value( 123.123 ).type( DECIMAL_NUMBER ).build() );
        dataSet.add( newData().name( "myDate" ).value( new DateMidnight( 2013, 1, 13 ) ).type( DATE ).build() );
        dataSet.add( newData().name( "myHtml" ).value( "<p>abc</p>" ).type( HTML_PART ).build() );

        // verify
        assertEquals( TEXT, dataSet.getData( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, dataSet.getData( "myNum" ).getType() );
        assertEquals( DATE, dataSet.getData( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, dataSet.getData( "myDec" ).getType() );
        assertEquals( HTML_PART, dataSet.getData( "myHtml" ).getType() );

        assertFalse( dataSet.getData( "myText" ) instanceof Text );
        assertFalse( dataSet.getData( "myNum" ) instanceof WholeNumber );
        assertFalse( dataSet.getData( "myDec" ) instanceof DecimalNumber );
        assertFalse( dataSet.getData( "myDate" ) instanceof Date );
        assertFalse( dataSet.getData( "myHtml" ) instanceof HtmlPart );
    }

    @Test
    public void dataSet_add_Data_using_newDataType()
    {
        DataSet dataSet = new RootDataSet();

        // exercise
        dataSet.add( newText().name( "myText" ).value( "abc" ).build() );
        dataSet.add( newWholeNumber().name( "myNum" ).value( 123L ).build() );
        dataSet.add( newDecimalNumber().name( "myDec" ).value( 123.123 ).build() );
        dataSet.add( newDate().name( "myDate" ).value( new DateMidnight( 2013, 1, 13 ) ).build() );
        dataSet.add( newHtmlPart().name( "myHtml" ).value( "<p>abc</p>" ).build() );

        // verify
        assertEquals( TEXT, dataSet.getData( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, dataSet.getData( "myNum" ).getType() );
        assertEquals( DATE, dataSet.getData( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, dataSet.getData( "myDec" ).getType() );
        assertEquals( HTML_PART, dataSet.getData( "myHtml" ).getType() );

        assertTrue( dataSet.getData( "myText" ) instanceof Text );
        assertTrue( dataSet.getData( "myNum" ) instanceof WholeNumber );
        assertTrue( dataSet.getData( "myDec" ) instanceof DecimalNumber );
        assertTrue( dataSet.getData( "myDate" ) instanceof Date );
        assertTrue( dataSet.getData( "myHtml" ) instanceof HtmlPart );
    }

    @Test
    public void dataSet_add_Data_using_new_DataType()
    {
        DataSet dataSet = new RootDataSet();

        // exercise
        dataSet.add( new Text( "myText", "abc" ) );
        dataSet.add( new WholeNumber( "myNum", 123L ) );
        dataSet.add( new DecimalNumber( "myDec", 123.123 ) );
        dataSet.add( new Date( "myDate", new DateMidnight( 2013, 1, 13 ) ) );
        dataSet.add( new HtmlPart( "myHtml", "<p>abc</p>" ) );

        // verify
        assertEquals( TEXT, dataSet.getData( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, dataSet.getData( "myNum" ).getType() );
        assertEquals( DATE, dataSet.getData( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, dataSet.getData( "myDec" ).getType() );
        assertEquals( HTML_PART, dataSet.getData( "myHtml" ).getType() );

        assertTrue( dataSet.getData( "myText" ) instanceof Text );
        assertTrue( dataSet.getData( "myNum" ) instanceof WholeNumber );
        assertTrue( dataSet.getData( "myDec" ) instanceof DecimalNumber );
        assertTrue( dataSet.getData( "myDate" ) instanceof Date );
        assertTrue( dataSet.getData( "myHtml" ) instanceof HtmlPart );
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
        dataSet.add( newData().name( "myText" ).value( "abc" ).type( TEXT ).build() );
        dataSet.add( newData().name( "myNum" ).value( 123L ).type( WHOLE_NUMBER ).build() );
        dataSet.add( newData().name( "myDec" ).value( 123.123 ).type( DECIMAL_NUMBER ).build() );
        dataSet.add( newData().name( "myDate" ).value( new DateMidnight( 2013, 1, 13 ) ).type( DATE ).build() );
        dataSet.add( newData().name( "myHtml" ).value( "<p>abc</p>" ).type( HTML_PART ).build() );

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
        dataSet.add( newData().name( "myText" ).value( "a" ).type( TEXT ).build() );
        dataSet.add( newData().name( "myText" ).value( "b" ).type( TEXT ).build() );
        dataSet.add( newData().name( "myText" ).value( "c" ).type( TEXT ).build() );

        // verify
        assertEquals( "a", dataSet.getData( "myText" ).getString( 0 ) );
        assertEquals( "b", dataSet.getData( "myText" ).getString( 1 ) );
        assertEquals( "c", dataSet.getData( "myText" ).getString( 2 ) );
    }

    @Test
    public void dataSet_setData_array()
    {
        DataSet dataSet = new RootDataSet();

        // exercise
        dataSet.setData( "myText", "a", "b", "c" );

        // verify
        assertEquals( "a", dataSet.getData( "myText" ).getString( 0 ) );
        assertEquals( "b", dataSet.getData( "myText" ).getString( 1 ) );
        assertEquals( "c", dataSet.getData( "myText" ).getString( 2 ) );
    }

}
