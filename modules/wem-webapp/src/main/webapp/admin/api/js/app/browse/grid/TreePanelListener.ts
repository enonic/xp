module api_app_browse_grid {

    export interface TreePanelListener {

        onSelectionChanged(event:TreeSelectionChangedEvent);

        onItemDoubleClicked(event:TreeItemDoubleClickedEvent);

        onShowContextMenu(event:TreeShowContextMenuEvent);
    }

    export interface TreeSelectionChangedEvent {

        selectionCount:number;

        selectedModels:Ext_data_Model[];
    }

    export interface TreeItemDoubleClickedEvent {

        clickedModel:Ext_data_Model;
    }

    export interface TreeShowContextMenuEvent {

        x:number;

        y:number;

    }
}