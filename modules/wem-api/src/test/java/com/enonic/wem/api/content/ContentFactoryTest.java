package com.enonic.wem.api.content;

import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemPath;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;

import static org.junit.Assert.*;


public class ContentFactoryTest
{
    @Test
    public void toObject()
    {
        // setup
        ContentData contentData = new ContentData();
        contentData.add( new Property.WholeNumber( "myNumber", 1 ) );
        contentData.add( new Property.Text( "myText", "text" ) );
        contentData.setProperty( "mySet.myOtherNumber", new Value.WholeNumber( 2 ) );

        Content content = Content.newContent().
            id( ContentId.from( "ABC-123" ) ).
            name( "myContent" ).
            displayName( "My Content" ).
            owner( UserKey.from( "mystore:someuser" ) ).
            modifier( UserKey.from( "mystore:someotheruser" ) ).
            createdTime( DateTime.parse( "2012-12-12T12:00:00" ) ).
            modifiedTime( DateTime.parse( "2012-12-12T13:00:00" ) ).
            type( QualifiedContentTypeName.from( "mymodule:mycty" ) ).
            contentData( contentData ).build();

        Item item = content.toItem( new ItemPath( "/parent" ) );

        // exercise
        Content translated = ContentFactory.fromItem( item );

        // verify
        assertEquals( content.getId(), translated.getId() );
        assertEquals( content.getName(), translated.getName() );
        assertEquals( content.getDisplayName(), translated.getDisplayName() );
        assertEquals( content.getOwner(), translated.getOwner() );
        assertEquals( content.getModifier(), translated.getModifier() );
        assertEquals( content.getType(), translated.getType() );

        ContentData expectedContentData = content.getContentData();
        ContentData translatedContentData = translated.getContentData();
        assertEquals( expectedContentData.getName(), translatedContentData.getName() );
        assertEquals( expectedContentData.getParent(), translatedContentData.getParent() );
        assertEquals( expectedContentData.getPath(), translatedContentData.getPath() );
        assertEquals( expectedContentData.size(), translatedContentData.size() );
        assertEquals( expectedContentData.getProperty( "myNumber" ), translatedContentData.getProperty( "myNumber" ) );
        assertEquals( expectedContentData.getProperty( "myText" ), translatedContentData.getProperty( "myText" ) );
        assertEquals( expectedContentData.getProperty( "mySet.myOtherNumber" ),
                      translatedContentData.getProperty( "mySet.myOtherNumber" ) );
    }
}
