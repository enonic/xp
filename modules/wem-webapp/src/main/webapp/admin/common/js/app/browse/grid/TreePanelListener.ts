module api_app_browse_grid {

    export interface TreePanelListener {

        onSelectionChanged(event:TreeSelectionChangedEvent);

        onSelect(event:TreeSelectEvent);

        onDeselect(event:TreeDeselectEvent);

        onItemDoubleClicked(event:TreeItemDoubleClickedEvent);

        onShowContextMenu(event:TreeShowContextMenuEvent);
    }

    export interface TreeSelectionChangedEvent {

        selectionCount:number;

        selectedModels:Ext_data_Model[];
    }

    export interface TreeSelectEvent {

        selectedModel:Ext_data_Model;

    }

    export interface TreeDeselectEvent {

        deselectedModel:Ext_data_Model;

    }

    export interface TreeItemDoubleClickedEvent {

        clickedModel:Ext_data_Model;
    }

    export interface TreeShowContextMenuEvent {

        x:number;

        y:number;

    }
}