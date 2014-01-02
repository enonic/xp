module api.app.browse {

    export interface ItemsSelectionPanelListener<M> extends api.event.Listener {

        onDeselected(item:BrowseItem<M>);

    }

}