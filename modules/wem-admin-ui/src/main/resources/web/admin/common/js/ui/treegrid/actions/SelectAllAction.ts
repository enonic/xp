module api.ui.treegrid.actions {

    import Action = api.ui.Action;
    import Grid = api.ui.grid.Grid;

    export class SelectAllAction extends Action {

        constructor(grid: Grid<TreeNode<Object>>) {
            var allCount = grid.getDataView().getLength();
            super("Select All (" + allCount + ")");
            this.setEnabled(true);
            this.onExecuted(() => {
                grid.selectAll();
            });
        }
    }
}
