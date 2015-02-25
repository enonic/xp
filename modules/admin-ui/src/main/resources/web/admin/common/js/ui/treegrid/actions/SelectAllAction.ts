module api.ui.treegrid.actions {

    import Action = api.ui.Action;

    export class SelectAllAction<DATA> extends Action {

        constructor(treeGrid: TreeGrid<DATA>) {
            var allCount = treeGrid.getGrid().getDataView().getLength();
            super("Select All (" + allCount + ")");
            this.setEnabled(true);
            this.onExecuted(() => {
                treeGrid.selectAll();
            });
        }
    }
}
