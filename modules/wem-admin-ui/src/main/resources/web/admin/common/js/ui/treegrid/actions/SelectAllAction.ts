module api.ui.treegrid.actions {

    import Action = api.ui.Action;
    import Grid = api.ui.grid.Grid;

    export class SelectAllAction extends Action {

        constructor(grid: Grid<TreeNode<Object>>) {
            super("Select All");
            this.setEnabled(true);
            this.onExecuted(() => {
                grid.selectAll();
            });
        }
    }
}
