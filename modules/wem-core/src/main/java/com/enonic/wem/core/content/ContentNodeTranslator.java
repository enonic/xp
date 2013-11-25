package com.enonic.wem.core.content;

import java.util.List;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.UpdateNode;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeEditor;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.schema.SchemaId;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.core.support.SerializerForFormItemToData;

public class ContentNodeTranslator
{
    public static final String FORM_PATH = "form";

    public static final String FORMITEMS_DATA_PATH = "formItems";

    public static final String FORMITEMS_FULL_PATH = "form.formItems";

    public static final String CONTENT_DATA_PATH = "contentdata";

    private static final SerializerForFormItemToData SERIALIZER_FOR_FORM_ITEM_TO_DATA = new SerializerForFormItemToData();

    private static final ContentPath TEMPORARY_PARENT_PATH = ContentPath.rootOf( SpaceName.temporary() );

    public static final String DISPLAY_NAME_PATH = "displayName";

    public static final String CONTENT_TYPE_PATH = "contentType";

    public static final String PARENT_CONTENT_PATH_PATH = "parentContentPath";

    public Node toNode( final Content content )
    {
        final NodePath parentItemPath = createParentItemPath( content );

        final RootDataSet rootDataSet = propertiesToRootDataSet( content );

        final EntityIndexConfig entityIndexConfig = ContentEntityIndexConfigFactory.create( rootDataSet );

        return Node.newNode().
            id( content.getId() != null ? EntityId.from( content.getId() ) : null ).
            name( ContentToNodeNameResolver.resolve( content ) ).
            parent( parentItemPath ).
            rootDataSet( rootDataSet ).
            entityIndexConfig( entityIndexConfig ).
            build();
    }

    private NodePath createParentItemPath( final Content content )
    {
        if ( content.isTemporary() )
        {
            return NodePath.newPath( TEMPORARY_PARENT_PATH.toString() ).build();
        }
        else
        {
            return NodePath.newPath( "/content" ).build();
        }
    }

    public RootDataSet propertiesToRootDataSet( final Content content )
    {
        //final Collection<Attachment> attachments = command.getAttachments();

        final RootDataSet rootDataSet = new RootDataSet();

        addContentProperties( content, rootDataSet );
        addContentData( content.getContentData(), rootDataSet );
        addForm( content, rootDataSet );

        return rootDataSet;
    }

    private void addContentProperties( final Content content, final RootDataSet rootDataSet )
    {
        addPropertyIfNotNull( rootDataSet, DISPLAY_NAME_PATH, content.getDisplayName() );
        addPropertyIfNotNull( rootDataSet, CONTENT_TYPE_PATH, content.getType().getContentTypeName() );
        addPropertyIfNotNull( rootDataSet, PARENT_CONTENT_PATH_PATH,
                              content.getPath().getParentPath() != null ? content.getPath().getParentPath().getRelativePath() : null );
    }

    private void addForm( final Content content, final RootDataSet rootDataSet )
    {
        final DataSet form = new DataSet( FORM_PATH );
        final DataSet formItems = new DataSet( FORMITEMS_DATA_PATH );
        form.add( formItems );

        for ( Data formData : SERIALIZER_FOR_FORM_ITEM_TO_DATA.serializeFormItems(
            content.getForm() != null ? content.getForm().getFormItems() : Form.newForm().build().getFormItems() ) )
        {
            formItems.add( formData );
        }
        rootDataSet.add( form );
    }

    private void addContentData( final ContentData contentData, final RootDataSet rootDataSet )
    {
        rootDataSet.add( contentData.toDataSet( CONTENT_DATA_PATH ) );
    }

    UpdateNode toUpdateNodeCommand( final SchemaId id, final NodeEditor editor )
    {
        return Commands.node().update().
            item( EntityId.from( id ) ).
            editor( editor );
    }

    NodeEditor toNodeEditor( final Content content )
    {
        final RootDataSet rootDataSet = new RootDataSet();

        addPropertyIfNotNull( rootDataSet, DISPLAY_NAME_PATH, content.getDisplayName() );
        addPropertyIfNotNull( rootDataSet, CONTENT_TYPE_PATH, content.getType().getContentTypeName() );
        addPropertyIfNotNull( rootDataSet, PARENT_CONTENT_PATH_PATH, content.getPath().getParentPath().toString() );

        final DataSet form = new DataSet( FORM_PATH );
        final DataSet formItems = new DataSet( FORMITEMS_DATA_PATH );
        form.add( formItems );
        final List<Data> dataList = SERIALIZER_FOR_FORM_ITEM_TO_DATA.serializeFormItems( content.getForm().getFormItems() );

        for ( final Data data : dataList )
        {
            formItems.add( data );
        }
        rootDataSet.add( form );

        return new NodeEditor()
        {
            @Override
            public Node.EditBuilder edit( final Node toBeEdited )
            {
                return Node.editNode( toBeEdited ).
                    name( content.getName() ).
                    // icon( content.getIcon() ).
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

    public Content fromNode( final Node node )
    {
        final DataSet formItemsAsDataSet = node.dataSet( FORMITEMS_FULL_PATH );
        final FormItems formItems = SERIALIZER_FOR_FORM_ITEM_TO_DATA.deserializeFormItems( formItemsAsDataSet );

        final Content.Builder builder = Content.newContent().
            id( ContentId.from( node.id().toString() ) ).
            name( node.name() ).
            form( Form.newForm().addFormItems( formItems ).build() ).
            createdTime( node.getCreatedTime() ).
            creator( node.getCreator() ).
            modifiedTime( node.getModifiedTime() ).
            modifier( node.getModifier() );

        if ( node.property( DISPLAY_NAME_PATH ) != null )
        {
            builder.displayName( node.property( DISPLAY_NAME_PATH ).getString() );
        }

        builder.type( ContentTypeName.from( node.property( CONTENT_TYPE_PATH ).getString() ) );

        if ( node.dataSet( CONTENT_DATA_PATH ) != null )
        {
            builder.contentData( new ContentData( node.dataSet( CONTENT_DATA_PATH ).toRootDataSet() ) );
        }

        return builder.build();
    }
}
