package com.enonic.wem.core.content;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.Name;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentName;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.attachment.CreateAttachment;
import com.enonic.wem.api.content.attachment.CreateAttachments;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeEditor;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;

public class ContentNodeTranslator
{
    private final static Logger LOG = LoggerFactory.getLogger( ContentNodeTranslator.class );

    private final ContentDataSerializer CONTENT_SERIALIZER = new ContentDataSerializer();

    private ContentTypeService contentTypeService;

    public CreateNodeParams toCreateNode( final CreateContentParams params )
    {
        if ( params.getName() == null || StringUtils.isEmpty( params.getName().toString() ) )
        {
            params.name( Name.ensureValidName( params.getDisplayName() ) );
        }

        final PropertyTree contentAsData = new PropertyTree();
        CONTENT_SERIALIZER.toData( params, contentAsData.getRoot() );

        final IndexConfigDocument indexConfigDocument = ContentIndexConfigFactory.create( params );

        final CreateNodeParams.Builder builder = CreateNodeParams.create().
            name( resolveNodeName( params.getName() ) ).
            parent( resolveParentNodePath( params.getParent() ) ).
            data( contentAsData ).
            indexConfigDocument( indexConfigDocument ).
            permissions( params.getPermissions() ).
            inheritPermissions( params.isInheritPermissions() ).
            nodeType( ContentConstants.CONTENT_NODE_COLLECTION );

        for ( final CreateAttachment attachment : params.getCreateAttachments() )
        {
            builder.attachBinary( attachment.getBinaryReference(), attachment.getByteSource() );
        }

        // TODO: Thumbnail?
        return builder.build();
    }

    public UpdateNodeParams toUpdateNodeCommand( final Content content, final CreateAttachments createAttachments )
    {
        final UpdateNodeParams.Builder builder = UpdateNodeParams.create().
            id( NodeId.from( content.getId() ) ).
            editor( toNodeEditor( content, createAttachments ) );

        if ( createAttachments != null )
        {
            for ( final CreateAttachment createAttachment : createAttachments )
            {
                builder.attachBinary( createAttachment.getBinaryReference(), createAttachment.getByteSource() );
            }
        }
        return builder.build();
    }

    public Contents fromNodes( final Nodes nodes )
    {
        final Contents.Builder contents = Contents.builder();

        for ( final Node node : nodes )
        {
            try
            {
                contents.add( doGetFromNode( node ) );
            }
            catch ( final Exception e )
            {
                LOG.error( "Failed to translate node [" + node.id().toString() + "] to content", e );
            }
        }

        return contents.build();
    }

    public Content fromNode( final Node node )
    {
        return doGetFromNode( node );
    }

    private Content doGetFromNode( final Node node )
    {
        final NodePath parentNodePath = node.path().getParentPath();
        final NodePath parentContentPathAsNodePath = parentNodePath.removeFromBeginning( ContentConstants.CONTENT_ROOT_PATH );
        final ContentPath parentContentPath = ContentPath.from( parentContentPathAsNodePath.toString() );

        final Content.Builder builder = CONTENT_SERIALIZER.fromData( node.data().getRoot() );
        builder.
            id( ContentId.from( node.id().toString() ) ).
            parentPath( parentContentPath ).
            name( node.name().toString() ).
            createdTime( node.getCreatedTime() ).
            creator( node.getCreator() ).
            modifiedTime( node.getModifiedTime() ).
            modifier( node.getModifier() ).
            hasChildren( node.getHasChildren() ).
            childOrder( node.getChildOrder() ).
            permissions( node.getPermissions() ).
            inheritPermissions( node.inheritsPermissions() );

        return builder.build();
    }

    private NodeEditor toNodeEditor( final Content content, final CreateAttachments createAttachments )
    {
        final PropertyTree data = new PropertyTree();
        CONTENT_SERIALIZER.toData( content, data.getRoot(), createAttachments );

        final IndexConfigDocument indexConfigDocument = ContentIndexConfigFactory.create( content );

        return editableNode -> {

            editableNode.name = NodeName.from( content.getName().toString() );
            editableNode.indexConfigDocument = indexConfigDocument;
            editableNode.data = data;
            editableNode.permissions = content.getPermissions();
            editableNode.inheritPermissions = content.inheritsPermissions();
        };
    }

    private String resolveNodeName( final ContentName name )
    {
        if ( name instanceof ContentName.Unnamed )
        {
            ContentName.Unnamed unnammed = (ContentName.Unnamed) name;
            if ( !unnammed.hasUniqueness() )
            {
                return ContentName.Unnamed.withUniqueness().toString();
            }
        }
        return name.toString();
    }

    private NodePath resolveParentNodePath( final ContentPath parentContentPath )
    {
        return NodePath.newPath( ContentConstants.CONTENT_ROOT_PATH ).elements( parentContentPath.toString() ).build();
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {
        return contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) );
    }

    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

}
