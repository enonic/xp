package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.core.content.serializer.ContentDataJsonSerializer;

import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_VERSION_HISTORY_NODE;
import static com.enonic.wem.core.content.dao.ContentDao.SPACES_PATH;
import static com.enonic.wem.core.content.dao.ContentDao.SPACE_CONTENT_ROOT_NODE;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyDateTime;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyLong;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyString;
import static com.enonic.wem.core.jcr.JcrHelper.setPropertyDateTime;
import static org.apache.commons.lang.StringUtils.removeStart;
import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.substringBefore;

final class ContentJcrMapper
{
    static final String TYPE = "type";

    static final String DATA = "data";

    static final String CREATED_TIME = "createdTime";

    static final String MODIFIED_TIME = "modifiedTime";

    static final String MODIFIER = "modifier";

    static final String OWNER = "owner";

    static final String DISPLAY_NAME = "displayName";

    static final String VERSION_ID = "versionId";

    private ContentDataJsonSerializer contentDataSerializer = new ContentDataJsonSerializer();

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

        final String dataAsJson = contentDataSerializer.toString( content.getContentData() );
        contentNode.setProperty( DATA, dataAsJson );
        setPropertyDateTime( contentNode, CREATED_TIME, content.getCreatedTime() );
        setPropertyDateTime( contentNode, MODIFIED_TIME, content.getModifiedTime() );
        contentNode.setProperty( MODIFIER, content.getModifier() == null ? null : content.getModifier().toString() );
        contentNode.setProperty( OWNER, content.getOwner() == null ? null : content.getOwner().toString() );
        contentNode.setProperty( DISPLAY_NAME, content.getDisplayName() );
        contentNode.setProperty( VERSION_ID, content.getVersionId().id() );
    }

    void toContent( final Node contentNode, final Content.Builder contentBuilder )
        throws RepositoryException
    {
        final String dataAsJson = contentNode.getProperty( DATA ).getString();
        final ContentData contentData = contentDataSerializer.toObject( dataAsJson );
        contentBuilder.contentData( contentData );

        contentBuilder.createdTime( getPropertyDateTime( contentNode, CREATED_TIME ) );
        contentBuilder.modifiedTime( getPropertyDateTime( contentNode, MODIFIED_TIME ) );
        if ( contentNode.hasProperty( MODIFIER ) )
        {
            contentBuilder.modifier( AccountKey.from( getPropertyString( contentNode, MODIFIER ) ).asUser() );
        }
        if ( contentNode.hasProperty( OWNER ) )
        {
            contentBuilder.owner( AccountKey.from( getPropertyString( contentNode, OWNER ) ).asUser() );
        }
        contentBuilder.modifiedTime( getPropertyDateTime( contentNode, MODIFIED_TIME ) );
        contentBuilder.displayName( getPropertyString( contentNode, DISPLAY_NAME ) );
        final String contentType = getPropertyString( contentNode, TYPE );
        if ( contentType != null )
        {
            contentBuilder.type( new QualifiedContentTypeName( contentType ) );
        }
        contentBuilder.id( ContentIdFactory.from( contentNode ) );
        contentBuilder.path( getPathFromNode( contentNode ) );
        if ( contentNode.hasProperty( VERSION_ID ) )
        {
            contentBuilder.version( ContentVersionId.of( getPropertyLong( contentNode, VERSION_ID ) ) );
        }
    }

    private ContentPath getPathFromNode( final Node contentNode )
        throws RepositoryException
    {
        final String contentNodePath;
        if ( contentNode.getParent().getName().equals( CONTENT_VERSION_HISTORY_NODE ) )
        {
            contentNodePath = contentNode.getParent().getParent().getPath();
        }
        else
        {
            contentNodePath = contentNode.getPath();
        }
        final String fullPath = substringAfter( contentNodePath, SPACES_PATH );
        final String spaceName = substringBefore( fullPath, "/" );
        final String relativePath = removeStart( substringAfter( fullPath, "/" ), SPACE_CONTENT_ROOT_NODE );
        return ContentPath.from( spaceName + ":" + relativePath );
    }
}
