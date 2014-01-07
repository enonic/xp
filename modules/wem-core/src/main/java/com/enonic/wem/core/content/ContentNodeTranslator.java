package com.enonic.wem.core.content;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.entity.CreateNode;
import com.enonic.wem.api.command.entity.UpdateNode;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentName;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeEditor;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.content.page.PageSerializer;
import com.enonic.wem.core.content.site.SiteSerializer;
import com.enonic.wem.core.support.SerializerForFormItemToData;

public class ContentNodeTranslator
{
    public static final String FORM_PATH = "form";

    public static final String FORMITEMS_DATA_PATH = "formItems";

    public static final String FORMITEMS_FULL_PATH = "form.formItems";

    public static final String CONTENT_DATA_PATH = "contentdata";

    public static final String PAGE_CONFIG_PATH = "page";

    private static final SerializerForFormItemToData SERIALIZER_FOR_FORM_ITEM_TO_DATA = new SerializerForFormItemToData();

    private static final ContentAttachmentNodeTranslator CONTENT_ATTACHMENT_NODE_TRANSLATOR = new ContentAttachmentNodeTranslator();

    private static final PageSerializer PAGE_SERIALIZER = new PageSerializer();

    private static final SiteSerializer SITE_SERIALIZER = new SiteSerializer();

    public static final String DRAFT_PATH = "draft";

    public static final String DISPLAY_NAME_PATH = "displayName";

    public static final String CONTENT_TYPE_PATH = "contentType";

    private static final NodePath CONTENTS_ROOT_PATH = NodePath.newPath( "/content" ).build();

    public static final String SITE_CONFIG_PATH = "site";

    public CreateNode toCreateNode( final CreateContent command )
    {
        final RootDataSet rootDataSet = toRootDataSet( command );
        final EntityIndexConfig entityIndexConfig = ContentEntityIndexConfigFactory.create( rootDataSet );

        final CreateNode createNode = new CreateNode();
        createNode.name( resolveNodeName( command.getName() ) );
        createNode.parent( resolveParentNodePath( command.getParentContentPath() ) );
        createNode.embed( command.isEmbed() );
        createNode.data( rootDataSet );
        createNode.attachments( CONTENT_ATTACHMENT_NODE_TRANSLATOR.toNodeAttachments( command.getAttachments() ) );
        createNode.entityIndexConfig( entityIndexConfig );
        return createNode;
    }

    private RootDataSet toRootDataSet( final CreateContent command )
    {
        final RootDataSet rootDataSet = new RootDataSet();

        addPropertyIfNotNull( rootDataSet, DRAFT_PATH, command.isDraft() );
        addPropertyIfNotNull( rootDataSet, DISPLAY_NAME_PATH, command.getDisplayName() );
        addPropertyIfNotNull( rootDataSet, CONTENT_TYPE_PATH, command.getContentType() );

        if ( command.getContentData() != null )
        {
            rootDataSet.add( command.getContentData().toDataSet( CONTENT_DATA_PATH ) );
        }

        addForm( command.getForm(), rootDataSet );

        return rootDataSet;
    }

    public RootDataSet propertiesToRootDataSet( final Content content )
    {
        final RootDataSet rootDataSet = new RootDataSet();

        addContentMetaData( content, rootDataSet );
        addContentData( content.getContentData(), rootDataSet );
        addForm( content.getForm(), rootDataSet );
        addPage( content.getPage(), rootDataSet );
        addSite( content.getSite(), rootDataSet );

        return rootDataSet;
    }

    private void addPage( final Page page, final RootDataSet rootDataSet )
    {
        final DataSet pageData = PAGE_SERIALIZER.toData( page, PAGE_CONFIG_PATH );

        if ( pageData != null )
        {
            rootDataSet.add( pageData );
        }
    }

    private void addSite( final Site site, final RootDataSet rootDataSet )
    {
        final DataSet siteData = SITE_SERIALIZER.toData( site, SITE_CONFIG_PATH );

        if ( siteData != null )
        {
            rootDataSet.add( siteData );
        }
    }

    private void addContentMetaData( final Content content, final RootDataSet rootDataSet )
    {
        addPropertyIfNotNull( rootDataSet, DRAFT_PATH, content.isDraft() );
        addPropertyIfNotNull( rootDataSet, DISPLAY_NAME_PATH, content.getDisplayName() );
        addPropertyIfNotNull( rootDataSet, CONTENT_TYPE_PATH, content.getType().getContentTypeName() );
    }

    private void addContentData( final ContentData contentData, final RootDataSet rootDataSet )
    {
        if ( contentData != null )
        {
            rootDataSet.add( contentData.toDataSet( CONTENT_DATA_PATH ) );
        }
    }

    private void addForm( final Form form, final RootDataSet rootDataSet )
    {
        if ( form != null )
        {
            final DataSet formDataSet = new DataSet( FORM_PATH );
            final DataSet formItems = new DataSet( FORMITEMS_DATA_PATH );
            formDataSet.add( formItems );

            for ( Data formData : SERIALIZER_FOR_FORM_ITEM_TO_DATA.serializeFormItems(
                form != null ? form.getFormItems() : Form.newForm().build().getFormItems() ) )
            {
                formItems.add( formData );
            }
            rootDataSet.add( formDataSet );
        }
    }

    public Contents fromNodes( final Nodes nodes )
    {
        final Contents.Builder contents = Contents.builder();

        for ( final Node node : nodes )
        {
            contents.add( doGetFromNode( node ) );
        }

        return contents.build();
    }

    public Content fromNode( final Node node )
    {
        return doGetFromNode( node );
    }

    private Content doGetFromNode( final Node node )
    {
        final DataSet formItemsAsDataSet = node.dataSet( FORMITEMS_FULL_PATH );
        final FormItems formItems = SERIALIZER_FOR_FORM_ITEM_TO_DATA.deserializeFormItems( formItemsAsDataSet );

        final Content.Builder builder = Content.newContent().
            id( ContentId.from( node.id() ) ).
            parentPath( ContentPath.from( node.path().getParentPath().removeFromBeginning( CONTENTS_ROOT_PATH ).toString() ) ).
            name( node.name().toString() ).
            form( Form.newForm().addFormItems( formItems ).build() ).
            createdTime( node.getCreatedTime() ).
            creator( node.getCreator() ).
            modifiedTime( node.getModifiedTime() ).
            modifier( node.getModifier() );

        if ( node.property( DISPLAY_NAME_PATH ) != null )
        {
            builder.displayName( node.property( DISPLAY_NAME_PATH ).getString() );
        }

        if ( node.property( CONTENT_TYPE_PATH ) != null )
        {
            builder.type( ContentTypeName.from( node.property( CONTENT_TYPE_PATH ).getString() ) );
        }

        if ( node.dataSet( CONTENT_DATA_PATH ) != null )
        {
            builder.contentData( new ContentData( node.dataSet( CONTENT_DATA_PATH ).toRootDataSet() ) );
        }

        if ( node.dataSet( PAGE_CONFIG_PATH ) != null )
        {
            builder.page( PAGE_SERIALIZER.toPage( node.dataSet( PAGE_CONFIG_PATH ) ) );
        }

        if ( node.dataSet( SITE_CONFIG_PATH ) != null )
        {
            builder.site( SITE_SERIALIZER.toSite( node.dataSet( SITE_CONFIG_PATH ) ) );
        }

        return builder.build();
    }

    public UpdateNode toUpdateNodeCommand( final ContentId id, final NodeEditor editor )
    {
        return Commands.node().update().
            item( EntityId.from( id.toString() ) ).
            editor( editor );
    }

    public NodeEditor toNodeEditor( final Content content, final UpdateContent updateContentCommand )
    {
        final RootDataSet rootDataSet = propertiesToRootDataSet( content );

        final EntityIndexConfig entityIndexConfig = ContentEntityIndexConfigFactory.create( rootDataSet );

        return new NodeEditor()
        {
            @Override
            public Node.EditBuilder edit( final Node toBeEdited )
            {
                return Node.editNode( toBeEdited ).
                    name( NodeName.from( content.getName().toString() ) ).
                    attachments( CONTENT_ATTACHMENT_NODE_TRANSLATOR.toNodeAttachments( updateContentCommand.getAttachments() ) ).
                    entityIndexConfig( entityIndexConfig ).
                    rootDataSet( rootDataSet );
            }
        };
    }

    private void addPropertyIfNotNull( final RootDataSet rootDataSet, final String propertyName, final Object value )
    {
        if ( value != null )
        {
            rootDataSet.setProperty( propertyName, new Value.String( value.toString() ) );
        }
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
        return NodePath.newPath( CONTENTS_ROOT_PATH ).elements( parentContentPath.toString() ).build();
    }

}
