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

            treeGrid.onSelectionChanged((selection) => {
                this.setLabel(this.createLabel(selection.length));
                this.setEnabled(!!selection.length);
            });
        }

        private createLabel(count: number): string {
            return "Clear Selection" + ( !!count ? " (" + count + ")" : "");
        }
    }
}
