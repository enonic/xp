package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.CreateNode;
import com.enonic.wem.api.command.entity.UpdateNode;
import com.enonic.wem.api.command.schema.content.CreateContentType;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityPropertyIndexConfig;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeEditor;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.schema.SchemaId;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.form.FormDataSerializer;
import com.enonic.wem.core.icon.IconDataSerializer;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;

public class ContentTypeNodeTranslator
{
    public static final String FORM_PATH = "form";

    private static final FormDataSerializer FORM_SERIALIZER = new FormDataSerializer( FORM_PATH );

    public static final String DISPLAY_NAME_PROPERTY = "displayName";

    public static final String ICON_DATA_SET = "icon";

    public static final String SUPER_TYPE_PROPERTY = "superType";

    public static final String CONTENT_DISPLAY_NAME_SCRIPT_PROPERTY = "contentDisplayNameScript";

    public static final String ALLOW_CHILD_CONTENT_PROPERTY = "allowChildContent";

    public static final String BUILT_IN_PROPERTY = "builtIn";

    public static final String ABSTRACT_PROPERTY = "abstract";

    public static final String FINAL_PROPERTY = "final";

    public CreateNode toCreateNodeCommand( final CreateContentType command )
    {
        final NodePath parentItemPath = NodePath.newPath( "/content-types" ).build();

        return createNode( command, parentItemPath );
    }

    private CreateNode createNode( final CreateContentType command, final NodePath parentItemPath )
    {
        final RootDataSet rootDataSet = propertiesToRootDataSet( command );
        final EntityPropertyIndexConfig entityIndexConfig = ContentTypeEntityIndexConfigFactory.create( rootDataSet );

        return Commands.node().create().
            name( command.getName().toString() ).
            parent( parentItemPath ).
            data( rootDataSet ).
            entityIndexConfig( entityIndexConfig );
    }

    public RootDataSet propertiesToRootDataSet( final CreateContentType command )
    {
        final RootDataSet contentTypeAsData = new RootDataSet();
        addPropertyIfNotNull( contentTypeAsData, DISPLAY_NAME_PROPERTY, command.getDisplayName() );
        IconDataSerializer.nullableToData( command.getIcon(), ICON_DATA_SET, contentTypeAsData );
        addPropertyIfNotNull( contentTypeAsData, SUPER_TYPE_PROPERTY, command.getSuperType() );
        addPropertyIfNotNull( contentTypeAsData, CONTENT_DISPLAY_NAME_SCRIPT_PROPERTY, command.getContentDisplayNameScript() );
        addPropertyIfNotNull( contentTypeAsData, ALLOW_CHILD_CONTENT_PROPERTY, Boolean.toString( command.getAllowChildContent() ) );
        addPropertyIfNotNull( contentTypeAsData, BUILT_IN_PROPERTY, Boolean.toString( command.isBuiltIn() ) );
        addPropertyIfNotNull( contentTypeAsData, ABSTRACT_PROPERTY, Boolean.toString( command.isAbstract() ) );
        addPropertyIfNotNull( contentTypeAsData, FINAL_PROPERTY, Boolean.toString( command.isFinal() ) );

        contentTypeAsData.add( FORM_SERIALIZER.toData( command.getForm() ) );

        return contentTypeAsData;
    }

    UpdateNode toUpdateNodeCommand( final SchemaId id, final NodeEditor editor )
    {
        return Commands.node().update().
            id( EntityId.from( id ) ).
            editor( editor );
    }

    NodeEditor toNodeEditor( final ContentType contentType )
    {
        final RootDataSet contentTypeAsData = new RootDataSet();
        addPropertyIfNotNull( contentTypeAsData, DISPLAY_NAME_PROPERTY, contentType.getDisplayName() );
        IconDataSerializer.nullableToData( contentType.getIcon(), ICON_DATA_SET, contentTypeAsData );
        addPropertyIfNotNull( contentTypeAsData, SUPER_TYPE_PROPERTY, contentType.getSuperType() );
        addPropertyIfNotNull( contentTypeAsData, CONTENT_DISPLAY_NAME_SCRIPT_PROPERTY, contentType.getContentDisplayNameScript() );
        addPropertyIfNotNull( contentTypeAsData, ALLOW_CHILD_CONTENT_PROPERTY, Boolean.toString( contentType.allowChildContent() ) );
        addPropertyIfNotNull( contentTypeAsData, BUILT_IN_PROPERTY, Boolean.toString( contentType.isBuiltIn() ) );
        addPropertyIfNotNull( contentTypeAsData, ABSTRACT_PROPERTY, Boolean.toString( contentType.isAbstract() ) );
        addPropertyIfNotNull( contentTypeAsData, FINAL_PROPERTY, Boolean.toString( contentType.isFinal() ) );

        contentTypeAsData.add( FORM_SERIALIZER.toData( contentType.form() ) );

        return new NodeEditor()
        {
            @Override
            public Node.EditBuilder edit( final Node toBeEdited )
            {
                return Node.editNode( toBeEdited ).
                    name( NodeName.from( contentType.getName().toString() ) ).
                    rootDataSet( contentTypeAsData );
            }
        };
    }

    ContentTypes fromNodes( final Nodes nodes )
    {
        final ContentTypes.Builder contentTypesBuilder = ContentTypes.newContentTypes();

        for ( final Node node : nodes )
        {
            contentTypesBuilder.add( fromNode( node ) );
        }

        return contentTypesBuilder.build();
    }

    ContentType fromNode( final Node node )
    {
        final RootDataSet contentTypeAsData = node.data();

        final ContentType.Builder builder = newContentType().
            id( new SchemaId( node.id().toString() ) ).
            name( node.name().toString() ).
            createdTime( node.getCreatedTime() ).
            creator( node.getCreator() ).
            modifiedTime( node.getModifiedTime() ).
            modifier( node.getModifier() ).
            icon( IconDataSerializer.toIconNullable( node.dataSet( ICON_DATA_SET ) ) );

        if ( contentTypeAsData.hasData( FORM_PATH ) )
        {
            builder.form( FORM_SERIALIZER.fromData( contentTypeAsData.getDataSet( FORM_PATH ) ) );
        }

        if ( contentTypeAsData.hasData( DISPLAY_NAME_PROPERTY ) )
        {
            builder.displayName( node.property( DISPLAY_NAME_PROPERTY ).getString() );
        }

        if ( contentTypeAsData.hasData( ALLOW_CHILD_CONTENT_PROPERTY ) )
        {
            builder.allowChildContent( Boolean.valueOf( node.property( ALLOW_CHILD_CONTENT_PROPERTY ).getString() ) );
        }

        if ( contentTypeAsData.hasData( CONTENT_DISPLAY_NAME_SCRIPT_PROPERTY ) )
        {
            builder.contentDisplayNameScript( node.property( CONTENT_DISPLAY_NAME_SCRIPT_PROPERTY ).getString() );
        }

        if ( contentTypeAsData.hasData( SUPER_TYPE_PROPERTY ) )
        {
            builder.superType( ContentTypeName.from( node.property( SUPER_TYPE_PROPERTY ).getString() ) );
        }

        if ( contentTypeAsData.hasData( BUILT_IN_PROPERTY ) )
        {
            builder.builtIn( Boolean.valueOf( node.property( BUILT_IN_PROPERTY ).getString() ) );
        }

        if ( contentTypeAsData.hasData( FINAL_PROPERTY ) )
        {
            builder.setFinal( Boolean.valueOf( node.property( FINAL_PROPERTY ).getString() ) );
        }
        if ( contentTypeAsData.hasData( ABSTRACT_PROPERTY ) )
        {
            builder.setAbstract( Boolean.valueOf( node.property( ABSTRACT_PROPERTY ).getString() ) );
        }

        return builder.build();
    }

    private void addPropertyIfNotNull( final RootDataSet rootDataSet, final String propertyName, final Object value )
    {
        if ( value != null )
        {
            rootDataSet.setProperty( propertyName, new Value.String( value.toString() ) );
        }
    }
}
