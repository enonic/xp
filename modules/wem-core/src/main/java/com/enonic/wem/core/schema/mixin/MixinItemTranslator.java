package com.enonic.wem.core.schema.mixin;


import java.util.List;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.CreateNode;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeEditor;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.UpdateNode;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.schema.SchemaId;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.support.SerializerForFormItemToData;

import static com.enonic.wem.api.entity.SetNodeEditor.newSetItemEditor;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;

class MixinItemTranslator
{
    private static final SerializerForFormItemToData SERIALIZER_FOR_FORM_ITEM_TO_DATA = new SerializerForFormItemToData();

    CreateNode toCreateItemCommand( final CreateMixin createMixin )
    {
        final NodePath parentItemPath = NodePath.newPath( "/mixins" ).build();

        return Commands.item().create().
            name( createMixin.getName() ).
            parent( parentItemPath ).
            icon( createMixin.getIcon() ).
            data( toRootDataSet( createMixin ) );
    }

    UpdateNode toUpdateItemCommand( final SchemaId id, final NodeEditor editor )
    {
        return Commands.item().update().
            item( EntityId.from( id ) ).
            editor( editor );
    }

    RootDataSet toRootDataSet( final CreateMixin createMixin )
    {
        final RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "displayName", new Value.String( createMixin.getDisplayName() ) );

        final DataSet formItems = new DataSet( "formItems" );
        for ( Data data : SERIALIZER_FOR_FORM_ITEM_TO_DATA.serializeFormItems( createMixin.getFormItems() ) )
        {
            formItems.add( data );
        }
        rootDataSet.add( formItems );

        return rootDataSet;
    }

    NodeEditor toItemEditor( final Mixin mixin )
    {
        final RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "displayName", new Value.String( mixin.getName() ) );
        final DataSet formItemsAsDataSet = new DataSet( "formItems" );
        final List<Data> dataList = SERIALIZER_FOR_FORM_ITEM_TO_DATA.serializeFormItems( mixin.getFormItems() );
        for ( final Data data : dataList )
        {
            formItemsAsDataSet.add( data );
        }
        rootDataSet.add( formItemsAsDataSet );

        return newSetItemEditor().
            name( mixin.getName() ).
            icon( mixin.getIcon() ).
            data( rootDataSet ).build();
    }

    Mixin fromItem( final Node node )
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
