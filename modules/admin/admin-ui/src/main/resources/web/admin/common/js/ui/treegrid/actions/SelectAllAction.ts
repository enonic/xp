module api.ui.treegrid.actions {

    import Action = api.ui.Action;

    export class SelectAllAction<DATA> extends Action {

        constructor(treeGrid: TreeGrid<DATA>) {
            super();

            this.createLabel(treeGrid);
            this.setEnabled(true);
            this.onExecuted(() => {
                treeGrid.selectAll();
            });

            treeGrid.onSelectionChanged((selection) => {
                this.createLabel(treeGrid);
                this.setEnabled(!!this.getCount(treeGrid));
            });
        }

        private createLabel(treeGrid: TreeGrid<DATA>) {
            let count = this.getCount(treeGrid);
            let label = "Select All" + ( !!count ? " (" + count + ")" : "");

            this.setLabel(label);
        }

        private getCount(treeGrid: TreeGrid<DATA>): number {
            var allCount = treeGrid.getGrid().getDataView().getLength();
            return allCount - treeGrid.getEmptyNodesCount();
        }
    }
}
