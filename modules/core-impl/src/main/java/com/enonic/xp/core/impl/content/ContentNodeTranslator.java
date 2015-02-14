package com.enonic.xp.core.impl.content;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentName;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentState;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.CreateContentTranslatorParams;
import com.enonic.wem.api.content.UpdateContentTranslatorParams;
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
import com.enonic.wem.api.schema.mixin.MixinService;

@Component(immediate = true, service = ContentNodeTranslator.class)
public class ContentNodeTranslator
{
    private final static Logger LOG = LoggerFactory.getLogger( ContentNodeTranslator.class );

    private ContentDataSerializer contentSerializer;

    public CreateNodeParams toCreateNodeParams( final CreateContentTranslatorParams params )
    {
        final PropertyTree contentAsData = new PropertyTree();
        contentSerializer.toCreateNodeData( params, contentAsData.getRoot() );

        final IndexConfigDocument indexConfigDocument = ContentIndexConfigFactory.create( params );

        final CreateNodeParams.Builder builder = CreateNodeParams.create().
            name( resolveNodeName( params.getName() ) ).
            parent( resolveParentNodePath( params.getParent() ) ).
            data( contentAsData ).
            indexConfigDocument( indexConfigDocument ).
            permissions( params.getPermissions() ).
            inheritPermissions( params.isInheritPermissions() ).
            childOrder( params.getChildOrder() ).
            nodeType( ContentConstants.CONTENT_NODE_COLLECTION );

        for ( final CreateAttachment attachment : params.getCreateAttachments() )
        {
            builder.attachBinary( attachment.getBinaryReference(), attachment.getByteSource() );
        }

        return builder.build();
    }

    public UpdateNodeParams toUpdateNodeParams( final UpdateContentTranslatorParams params )
    {
        final Content editedContent = params.getEditedContent();
        final CreateAttachments createAttachments = params.getCreateAttachments();

        final NodeEditor nodeEditor = toNodeEditor( params );

        final UpdateNodeParams.Builder builder = UpdateNodeParams.create().
            id( NodeId.from( editedContent.getId() ) ).
            editor( nodeEditor );

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

        final Content.Builder builder = contentSerializer.fromData( node.data().getRoot() );
        builder.
            id( ContentId.from( node.id().toString() ) ).
            parentPath( parentContentPath ).
            name( node.name().toString() ).
            hasChildren( node.getHasChildren() ).
            childOrder( node.getChildOrder() ).
            permissions( node.getPermissions() ).
            inheritPermissions( node.inheritsPermissions() ).
            contentState( ContentState.from( node.getNodeState().value() ) );

        return builder.build();
    }

    private NodeEditor toNodeEditor( final UpdateContentTranslatorParams params )
    {
        final Content content = params.getEditedContent();

        final PropertyTree nodeData = contentSerializer.toNodeData( params );

        final IndexConfigDocument indexConfigDocument = ContentIndexConfigFactory.create( content );

        return editableNode -> {
            editableNode.name = NodeName.from( content.getName().toString() );
            editableNode.indexConfigDocument = indexConfigDocument;
            editableNode.data = nodeData;
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

    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.contentSerializer = new ContentDataSerializer( mixinService );
    }
}
