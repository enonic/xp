module api.ui.treegrid {

    import Action = api.ui.Action;
    import Grid = api.ui.grid.Grid;

    export class SelectAllAction extends Action {

        constructor(grid: Grid<TreeNode<TreeItem>>) {
            super("Select All");
            this.setEnabled(true);
            this.onExecuted(() => {
                grid.selectAll();
            });
        }
    }
}
