module api_app_browse {

    export interface ItemsSelectionPanelListener<M> extends api_event.Listener {

        onDeselected(item:BrowseItem<M>);

    }

}