module api.ui.treegrid.actions {

    import Action = api.ui.Action;

    export class ClearSelectionAction<DATA> extends Action {

        constructor(treeGrid: TreeGrid<DATA>, count?: number) {
            super(this.createLabel(count));

            this.setEnabled(true);
            this.onExecuted(() => {
                treeGrid.getRoot().clearStashedSelection();
                treeGrid.getGrid().clearSelection();
            });

            treeGrid.onSelectionChanged(() => {
                var selectedCount = treeGrid.getRoot().getFullSelection().length;
                this.setLabel(this.createLabel(selectedCount));
                this.setEnabled(!!selectedCount);
            });
        }

        private createLabel(count: number): string {
            return "Clear Selection" + ( !!count ? " (" + count + ")" : "");
        }
    }
}
