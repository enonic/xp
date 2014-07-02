module api.ui.treegrid {

    import Action = api.ui.Action;
    import Grid = api.ui.grid.Grid;

    export class ClearSelectionAction extends Action {

        constructor(grid: Grid<TreeNode<TreeItem>>) {
            super("Clear Selection");
            this.setEnabled(true);
            this.onExecuted(() => {
                grid.clearSelection();
            });
        }
    }
}
