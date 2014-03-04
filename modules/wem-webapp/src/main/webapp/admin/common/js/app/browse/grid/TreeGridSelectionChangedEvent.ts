module  api.app.browse.grid {

    export class TreeGridSelectionChangedEvent {

        private selectionCount: number;

        private selectedModels: Ext_data_Model[];

        constructor(selectedModels: Ext_data_Model[], selectionCount: number) {
            this.selectedModels = selectedModels;
            this.selectionCount = selectionCount;
        }

        getSelectionCount(): number {
            return this.selectionCount;
        }

        getSelectedModels(): Ext_data_Model[] {
            return this.selectedModels;
        }
    }
}