module api.app.browse.action {

    import Action = api.ui.Action;
    import TreeGrid = api.ui.treegrid.TreeGrid;

    export class ShowAllAction<DATA> extends Action {

        constructor(show: () => void, treeGrid: TreeGrid<DATA>) {
            super();

            this.createLabel(treeGrid);

            this.setEnabled(true);

            this.onExecuted(show);

            treeGrid.onSelectionChanged(() => {
                const selectedCount = treeGrid.getRoot().getFullSelection().length;
                this.createLabel(treeGrid);
                this.setEnabled(selectedCount > 0);
            });
        }

        private createLabel(treeGrid: TreeGrid<DATA>) {
            let selectedCount = treeGrid.getRoot().getFullSelection().length;
            let label = selectedCount > 0 ? `Show All (${selectedCount})` : 'Show All';

            this.setLabel(label);
        }
    }
}
