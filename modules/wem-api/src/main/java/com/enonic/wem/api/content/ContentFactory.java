package com.enonic.wem.api.content;


import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;

import static com.enonic.wem.api.content.Content.newContent;

public class ContentFactory
{
    public static Content fromItem( final Item item )
    {

        return newContent().
            id( ContentId.from( item.id().toString() ) ).
            name( item.name() ).
            displayName( item.property( "displayName" ).getString() ).
            createdTime( item.getCreatedTime() ).
            creator( item.getCreator() ).
            modifiedTime( item.getModifiedTime() ).
            modifier( item.getModifier() ).
            owner( AccountKey.from( item.property( "owner" ).getString() ).asUser() ).
            type( QualifiedContentTypeName.from( item.property( "type" ).getString() ) ).
            contentData( new ContentData( item.dataSet( "data" ).toRootDataSet() ) ).
            build();
    }
}
