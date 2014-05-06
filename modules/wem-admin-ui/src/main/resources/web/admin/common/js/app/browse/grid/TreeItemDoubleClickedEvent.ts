module api.app.browse.grid {

    export class TreeItemDoubleClickedEvent {

        private selectedModel: Ext_data_Model;

        constructor(selectedModel: Ext_data_Model) {
            this.selectedModel = selectedModel;
        }

        getSelectedModel(): Ext_data_Model {
            return this.selectedModel;
        }
    }
}