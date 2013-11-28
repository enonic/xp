package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.content.serializer.ContentDataJsonSerializer;
import com.enonic.wem.core.schema.content.serializer.FormItemsJsonSerializer;

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
    static final String DRAFT = "draft";

    static final String TYPE = "type";

    static final String FORM = "form";

    static final String DATA = "data";

    static final String CREATED_TIME = "createdTime";

    static final String MODIFIED_TIME = "modifiedTime";

    static final String CREATOR = "creator";

    static final String MODIFIER = "modifier";

    static final String OWNER = "owner";

    static final String DISPLAY_NAME = "displayName";

    static final String VERSION_ID = "versionId";

    private FormItemsJsonSerializer formItemsJsonSerializer = new FormItemsJsonSerializer();

    private ContentDataJsonSerializer contentDataSerializer = new ContentDataJsonSerializer();

    void toJcr( final Content content, final Node contentNode )
        throws RepositoryException
    {
        contentNode.setProperty( DRAFT, content.isDraft() );
        contentNode.setProperty( TYPE, content.getType() != null ? content.getType().toString() : null );
        contentNode.setProperty( FORM, content.getForm() != null ? formItemsJsonSerializer.toString( content.getForm() ) : null );
        contentNode.setProperty( DATA, contentDataSerializer.toString( content.getContentData() ) );
        setPropertyDateTime( contentNode, CREATED_TIME, content.getCreatedTime() );
        setPropertyDateTime( contentNode, MODIFIED_TIME, content.getModifiedTime() );
        contentNode.setProperty( CREATOR, content.getModifier() == null ? null : content.getCreator().toString() );
        contentNode.setProperty( MODIFIER, content.getModifier() == null ? null : content.getModifier().toString() );
        contentNode.setProperty( OWNER, content.getOwner() == null ? null : content.getOwner().toString() );
        contentNode.setProperty( DISPLAY_NAME, content.getDisplayName() );
        contentNode.setProperty( VERSION_ID, content.getVersionId().id() );
    }

    void toContent( final Node contentNode, final Content.Builder contentBuilder )
        throws RepositoryException
    {
        contentBuilder.draft( contentNode.getProperty( DRAFT ).getBoolean() );
        if ( contentNode.hasProperty( FORM ) )
        {
            final Form form =
                Form.newForm().addFormItems( formItemsJsonSerializer.toObject( contentNode.getProperty( FORM ).getString() ) ).build();
            contentBuilder.form( form );
        }

        final ContentData contentData = contentDataSerializer.toObject( contentNode.getProperty( DATA ).getString() );
        contentBuilder.contentData( contentData );

        contentBuilder.createdTime( getPropertyDateTime( contentNode, CREATED_TIME ) );
        contentBuilder.modifiedTime( getPropertyDateTime( contentNode, MODIFIED_TIME ) );
        if ( contentNode.hasProperty( MODIFIER ) )
        {
            contentBuilder.modifier( AccountKey.from( getPropertyString( contentNode, MODIFIER ) ).asUser() );
        }
        if ( contentNode.hasProperty( CREATOR ) )
        {
            contentBuilder.modifier( AccountKey.from( getPropertyString( contentNode, CREATOR ) ).asUser() );
        }
        if ( contentNode.hasProperty( OWNER ) )
        {
            contentBuilder.owner( AccountKey.from( getPropertyString( contentNode, OWNER ) ).asUser() );
        }
        contentBuilder.displayName( getPropertyString( contentNode, DISPLAY_NAME ) );
        final String contentType = getPropertyString( contentNode, TYPE );
        if ( contentType != null )
        {
            contentBuilder.type( ContentTypeName.from( contentType ) );
        }
        contentBuilder.id( ContentIdFactory.from( contentNode ) );
        contentBuilder.path( getPathFromNode( contentNode ) );
        if ( contentNode.hasProperty( VERSION_ID ) )
        {
            contentBuilder.version( ContentVersionId.of( getPropertyLong( contentNode, VERSION_ID ) ) );
        }
        NodeIterator children = contentNode.getNodes();
        while ( children.hasNext() )
        {
            Node child = children.nextNode();
            if ( !ContentJcrHelper.isNonContentNode( child ) )
            {
                contentBuilder.addChildId( ContentIdFactory.from( child ) );
            }
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
