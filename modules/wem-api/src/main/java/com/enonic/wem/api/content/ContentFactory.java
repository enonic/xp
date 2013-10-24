package com.enonic.wem.api.content;


import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;

import static com.enonic.wem.api.content.Content.newContent;

public class ContentFactory
{
    public static Content fromItem( final Node node )
    {

        return newContent().
            id( ContentId.from( node.id().toString() ) ).
            name( node.name() ).
            displayName( node.property( "displayName" ).getString() ).
            createdTime( node.getCreatedTime() ).
            creator( node.getCreator() ).
            modifiedTime( node.getModifiedTime() ).
            modifier( node.getModifier() ).
            owner( AccountKey.from( node.property( "owner" ).getString() ).asUser() ).
            type( QualifiedContentTypeName.from( node.property( "type" ).getString() ) ).
            contentData( new ContentData( node.dataSet( "data" ).toRootDataSet() ) ).
            build();
    }
}
