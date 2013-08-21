module api_app_browse {

    export interface ItemsSelectionPanelListener extends api_event.Listener {

        onDeselected(item:BrowseItem);

    }

}