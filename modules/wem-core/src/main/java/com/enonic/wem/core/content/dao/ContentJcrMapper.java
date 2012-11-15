package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.core.content.data.ContentDataSerializerJson;

import static com.enonic.wem.core.jcr.JcrHelper.getPropertyDateTime;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyString;
import static com.enonic.wem.core.jcr.JcrHelper.setPropertyDateTime;

final class ContentJcrMapper
{
    private static final String TYPE = "type";

    private static final String DATA = "data";

    private static final String CREATED_TIME = "createdTime";

    private static final String MODIFIED_TIME = "modifiedTime";

    private static final String MODIFIER = "modifier";

    private static final String OWNER = "owner";

    private static final String DISPLAY_NAME = "displayName";

    private ContentDataSerializerJson contentDataSerializerJson = new ContentDataSerializerJson();

    void toJcr( final Content content, final Node contentNode )
        throws RepositoryException
    {
        if ( content.getType() != null )
        {
            contentNode.setProperty( TYPE, content.getType().toString() );
        }
        else
        {
            contentNode.setProperty( TYPE, (String) null );
        }

        final String dataAsJson = contentDataSerializerJson.toString( content.getData() );
        contentNode.setProperty( DATA, dataAsJson );
        setPropertyDateTime( contentNode, CREATED_TIME, content.getCreatedTime() );
        setPropertyDateTime( contentNode, MODIFIED_TIME, content.getModifiedTime() );
        contentNode.setProperty( MODIFIER, content.getModifier() == null ? null : content.getModifier().toString() );
        contentNode.setProperty( OWNER, content.getOwner() == null ? null : content.getOwner().toString() );
        contentNode.setProperty( DISPLAY_NAME, content.getDisplayName() );
    }

    void toContent( final Node contentNode, final Content content )
        throws RepositoryException
    {
        final String dataAsJson = contentNode.getProperty( DATA ).getString();
        final ContentData contentData = contentDataSerializerJson.toObject( dataAsJson );
        content.setData( contentData );

        content.setCreatedTime( getPropertyDateTime( contentNode, CREATED_TIME ) );
        content.setModifiedTime( getPropertyDateTime( contentNode, MODIFIED_TIME ) );
        if ( contentNode.hasProperty( MODIFIER ) )
        {
            content.setModifier( AccountKey.from( getPropertyString( contentNode, MODIFIER ) ).asUser() );
        }
        if ( contentNode.hasProperty( OWNER ) )
        {
            content.setOwner( AccountKey.from( getPropertyString( contentNode, OWNER ) ).asUser() );
        }
        content.setModifiedTime( getPropertyDateTime( contentNode, MODIFIED_TIME ) );
        content.setDisplayName( getPropertyString( contentNode, DISPLAY_NAME ) );
        final String contentType = getPropertyString( contentNode, TYPE );
        if ( contentType != null )
        {
            content.setType( new QualifiedContentTypeName( contentType ) );
        }
    }
}
