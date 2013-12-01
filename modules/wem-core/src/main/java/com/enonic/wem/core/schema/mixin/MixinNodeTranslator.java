package com.enonic.wem.core.schema.mixin;


import java.util.List;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.CreateNode;
import com.enonic.wem.api.command.entity.UpdateNode;
import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeEditor;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.schema.SchemaId;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.core.support.SerializerForFormItemToData;

import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;

class MixinNodeTranslator
{
    private static final SerializerForFormItemToData SERIALIZER_FOR_FORM_ITEM_TO_DATA = new SerializerForFormItemToData();

    public static final String DISPLAY_NAME_PROPERTY = "displayName";

    CreateNode toCreateNodeCommand( final CreateMixin createMixin )
    {
        final NodePath parentItemPath = NodePath.newPath( "/mixins" ).build();

        final RootDataSet rootDataSet = toRootDataSet( createMixin );
        final EntityIndexConfig indexConfig = MixinEntityIndexConfigFactory.create( rootDataSet );

        return Commands.node().create().
            name( createMixin.getName().toString() ).
            parent( parentItemPath ).
            icon( createMixin.getIcon() ).
            data( rootDataSet ).
            entityIndexConfig( indexConfig );
    }

    RootDataSet toRootDataSet( final CreateMixin createMixin )
    {
        final RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( DISPLAY_NAME_PROPERTY, new Value.String( createMixin.getDisplayName() ) );

        final DataSet formItems = new DataSet( "formItems" );
        for ( Data data : SERIALIZER_FOR_FORM_ITEM_TO_DATA.serializeFormItems( createMixin.getFormItems() ) )
        {
            formItems.add( data );
        }
        rootDataSet.add( formItems );

        return rootDataSet;
    }

    UpdateNode toUpdateNodeCommand( final SchemaId id, final NodeEditor editor )
    {
        return Commands.node().update().
            item( EntityId.from( id ) ).
            editor( editor );
    }

    NodeEditor toNodeEditor( final Mixin mixin )
    {
        final RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "displayName", new Value.String( mixin.getDisplayName() ) );
        final DataSet formItemsAsDataSet = new DataSet( "formItems" );
        final List<Data> dataList = SERIALIZER_FOR_FORM_ITEM_TO_DATA.serializeFormItems( mixin.getFormItems() );
        for ( final Data data : dataList )
        {
            formItemsAsDataSet.add( data );
        }
        rootDataSet.add( formItemsAsDataSet );

        return new NodeEditor()
        {
            @Override
            public Node.EditBuilder edit( final Node toBeEdited )
            {
                return Node.editNode( toBeEdited ).
                    name( mixin.getName().toString() ).
                    icon( mixin.getIcon() ).
                    rootDataSet( rootDataSet );
            }
        };
    }

    Mixins fromNodes( final Nodes nodes )
    {
        Mixins.Builder mixins = Mixins.newMixins();
        for ( Node node : nodes )
        {
            mixins.add( fromNode( node ) );
        }
        return mixins.build();
    }

    Mixin fromNode( final Node node )
    {
        final DataSet formItemsAsDataSet = node.dataSet( "formItems" );
        final FormItems formItems = SERIALIZER_FOR_FORM_ITEM_TO_DATA.deserializeFormItems( formItemsAsDataSet );

        return newMixin().
            id( new SchemaId( node.id().toString() ) ).
            name( node.name() ).
            displayName( node.property( "displayName" ).getString() ).
            formItems( formItems ).
            createdTime( node.getCreatedTime() ).
            creator( node.getCreator() ).
            modifiedTime( node.getModifiedTime() ).
            modifier( node.getModifier() ).
            icon( node.icon() ).
            build();
    }
}
