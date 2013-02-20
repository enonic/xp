package com.enonic.wem.api.content;


import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Date;
import com.enonic.wem.api.content.data.DecimalNumber;
import com.enonic.wem.api.content.data.HtmlPart;
import com.enonic.wem.api.content.data.Text;
import com.enonic.wem.api.content.data.WholeNumber;

import static com.enonic.wem.api.content.Content.newContent;
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
    public void content_setData()
    {
        Content content = newContent().name( "myContent" ).build();
        content.setData( "myText", "abc" );
        content.setData( "myNum", 123L );
        content.setData( "myDate", new DateMidnight( 2013, 1, 13 ) );
        content.setData( "myDec", 123.123 );
        content.setData( "myHtml", "<p>abc</p>", HTML_PART );

        assertEquals( TEXT, content.getData( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, content.getData( "myNum" ).getType() );
        assertEquals( DATE, content.getData( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, content.getData( "myDec" ).getType() );
        assertEquals( HTML_PART, content.getData( "myHtml" ).getType() );
    }

    @Test
    public void dataSet_setData()
    {
        Content content = newContent().name( "myContent" ).build();
        DataSet dataSet = content.getRootDataSet();
        dataSet.setData( "myText", "abc" );
        dataSet.setData( "myNum", 123L );
        dataSet.setData( "myDate", new DateMidnight( 2013, 1, 13 ) );
        dataSet.setData( "myDec", 123.123 );
        content.setData( "myHtml", "<p>abc</p>", HTML_PART );

        assertEquals( TEXT, content.getData( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, content.getData( "myNum" ).getType() );
        assertEquals( DATE, content.getData( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, content.getData( "myDec" ).getType() );
        assertEquals( HTML_PART, content.getData( "myHtml" ).getType() );
    }

    @Test
    public void dataSet_add_Data_using_newData()
    {
        Content content = newContent().name( "myContent" ).build();
        DataSet dataSet = content.getRootDataSet();
        dataSet.add( newData().name( "myText" ).value( "abc" ).type( TEXT ).build() );
        dataSet.add( newData().name( "myNum" ).value( 123L ).type( WHOLE_NUMBER ).build() );
        dataSet.add( newData().name( "myDec" ).value( 123.123 ).type( DECIMAL_NUMBER ).build() );
        dataSet.add( newData().name( "myDate" ).value( new DateMidnight( 2013, 1, 13 ) ).type( DATE ).build() );
        dataSet.add( newData().name( "myHtml" ).value( "<p>abc</p>" ).type( HTML_PART ).build() );

        assertEquals( TEXT, content.getData( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, content.getData( "myNum" ).getType() );
        assertEquals( DATE, content.getData( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, content.getData( "myDec" ).getType() );
        assertEquals( HTML_PART, content.getData( "myHtml" ).getType() );

        assertFalse( content.getData( "myText" ) instanceof Text );
        assertFalse( content.getData( "myNum" ) instanceof WholeNumber );
        assertFalse( content.getData( "myDec" ) instanceof DecimalNumber );
        assertFalse( content.getData( "myDate" ) instanceof Date );
        assertFalse( content.getData( "myHtml" ) instanceof HtmlPart );
    }

    @Test
    public void dataSet_add_Data_using_newDataType()
    {
        Content content = newContent().name( "myContent" ).build();
        DataSet dataSet = content.getRootDataSet();
        dataSet.add( newText().name( "myText" ).value( "abc" ).build() );
        dataSet.add( newWholeNumber().name( "myNum" ).value( 123L ).build() );
        dataSet.add( newDecimalNumber().name( "myDec" ).value( 123.123 ).build() );
        dataSet.add( newDate().name( "myDate" ).value( new DateMidnight( 2013, 1, 13 ) ).build() );
        dataSet.add( newHtmlPart().name( "myHtml" ).value( "<p>abc</p>" ).build() );

        assertEquals( TEXT, content.getData( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, content.getData( "myNum" ).getType() );
        assertEquals( DATE, content.getData( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, content.getData( "myDec" ).getType() );
        assertEquals( HTML_PART, content.getData( "myHtml" ).getType() );

        assertTrue( content.getData( "myText" ) instanceof Text );
        assertTrue( content.getData( "myNum" ) instanceof WholeNumber );
        assertTrue( content.getData( "myDec" ) instanceof DecimalNumber );
        assertTrue( content.getData( "myDate" ) instanceof Date );
        assertTrue( content.getData( "myHtml" ) instanceof HtmlPart );
    }

    @Test
    public void dataSet_add_Data_using_new_DataType()
    {
        Content content = newContent().name( "myContent" ).build();
        DataSet dataSet = content.getRootDataSet();
        dataSet.add( new Text( "myText", "abc" ) );
        dataSet.add( new WholeNumber( "myNum", 123L ) );
        dataSet.add( new DecimalNumber( "myDec", 123.123 ) );
        dataSet.add( new Date( "myDate", new DateMidnight( 2013, 1, 13 ) ) );
        dataSet.add( new HtmlPart( "myHtml", "<p>abc</p>" ) );

        assertEquals( TEXT, content.getData( "myText" ).getType() );
        assertEquals( WHOLE_NUMBER, content.getData( "myNum" ).getType() );
        assertEquals( DATE, content.getData( "myDate" ).getType() );
        assertEquals( DECIMAL_NUMBER, content.getData( "myDec" ).getType() );
        assertEquals( HTML_PART, content.getData( "myHtml" ).getType() );

        assertTrue( content.getData( "myText" ) instanceof Text );
        assertTrue( content.getData( "myNum" ) instanceof WholeNumber );
        assertTrue( content.getData( "myDec" ) instanceof DecimalNumber );
        assertTrue( content.getData( "myDate" ) instanceof Date );
        assertTrue( content.getData( "myHtml" ) instanceof HtmlPart );
    }

    @Test
    public void dataSet_add_DataSet()
    {
        Content content = newContent().name( "myContent" ).build();
        DataSet dataSet = newDataSet().name( "mySet" ).build();
        content.getRootDataSet().add( dataSet );

        assertNotNull( content.getRootDataSet().getDataSet( "mySet" ) );
    }

}
