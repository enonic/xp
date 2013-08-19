module api_app_browse_grid {

    export interface TreeGridPanelListener {

        onSelectionChanged?(event:TreeGridSelectionChangedEvent);

        onItemDoubleClicked?(event:TreeGridItemDoubleClickedEvent);
    }

    export interface TreeGridSelectionChangedEvent {

        selectionCount:number;

        selectedModels:Ext_data_Model[];
    }

    export interface TreeGridItemDoubleClickedEvent {

        clickedModel:Ext_data_Model;
    }
}