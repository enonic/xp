module api_app_browse_grid {

    export interface GridPanelListener {

        onSelectionChanged(event:GridSelectionChangedEvent);

        onSelect(event:GridSelectEvent);

        onDeselect(event:GridDeselectEvent);

        onItemDoubleClicked(event:TreeItemDoubleClickedEvent);

        onShowContextMenu(event:GridShowContextMenuEvent);
    }

    export interface GridSelectionChangedEvent {

        selectionCount:number;

        selectedModels:Ext_data_Model[];
    }

    export interface GridSelectEvent {

        selectedModel:Ext_data_Model;

    }

    export interface GridDeselectEvent {

        deselectedModel:Ext_data_Model;

    }

    export interface GridItemDoubleClickedEvent {

        clickedModel:Ext_data_Model;
    }

    export interface GridShowContextMenuEvent {

        x:number;

        y:number;
    }
}