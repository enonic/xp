module api_app_browse_grid {

    export interface GridPanelListener {

        onSelectionChanged?(event:GridSelectionChangedEvent);

        onShowContextMenu?(event:GridShowContextMenuEvent);
    }

    export interface GridSelectionChangedEvent {

        selectionCount:number;

        selectedModels:Ext_data_Model[];
    }

    export interface GridShowContextMenuEvent {

        x:number;

        y:number;
    }
}