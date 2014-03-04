module api.app.browse.grid {

    export class GridSelectionChangedEvent {

        private selectionCount: number;

        private selectedModels: Ext_data_Model[];

        constructor(selectionCount: number, selectedModels: Ext_data_Model[]) {
            this.selectedModels = selectedModels;
            this.selectionCount = selectionCount;
        }

        getSelectedModels(): Ext_data_Model[] {
            return this.selectedModels;
        }

        getSelectionCount(): number {
            return this.selectionCount;
        }
    }
}