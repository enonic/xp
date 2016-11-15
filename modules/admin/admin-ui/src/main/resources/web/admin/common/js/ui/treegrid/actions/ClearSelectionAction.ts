module api.ui.treegrid.actions {

    import Action = api.ui.Action;

    export class ClearSelectionAction<DATA> extends Action {

        constructor(treeGrid: TreeGrid<DATA>) {
            super();

            this.createLabel(treeGrid);
            this.setEnabled(true);
            this.onExecuted(() => {
                treeGrid.getRoot().clearStashedSelection();
                treeGrid.getGrid().clearSelection();
            });

            treeGrid.onSelectionChanged(() => {
                this.createLabel(treeGrid);
                this.setEnabled(!!this.getCount(treeGrid));
            });
        }

        private createLabel(treeGrid: TreeGrid<DATA>) {
            let count = this.getCount(treeGrid);
            let label = "Clear Selection" + ( !!count ? " (" + count + ")" : "");

            this.setLabel(label);
        }

        private getCount(treeGrid: TreeGrid<DATA>): number {
            return treeGrid.getRoot().getFullSelection().length;
        }
    }
}
