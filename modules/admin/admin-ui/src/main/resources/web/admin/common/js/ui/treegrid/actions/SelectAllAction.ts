module api.ui.treegrid.actions {

    import Action = api.ui.Action;

    export class SelectAllAction<DATA> extends Action {

        private treeGrid: TreeGrid<DATA>;

        constructor(treeGrid: TreeGrid<DATA>) {
            this.treeGrid = treeGrid;

            super(this.createLabel(this.getAvailableCount()));

            this.setEnabled(true);
            this.onExecuted(() => {
                treeGrid.selectAll();
            });

            treeGrid.onSelectionChanged((selection) => {
                var count = this.getAvailableCount();
                this.setLabel(this.createLabel(count));
                this.setEnabled(!!count);
            });
        }

        private createLabel(count: number): string {

            return "Select All" + ( !!count ? " (" + count + ")" : "");
        }

        private getAvailableCount(): number {
            var allCount = this.treeGrid.getGrid().getDataView().getLength();
            return allCount - this.treeGrid.getEmptyNodesCount();
        }
    }
}
