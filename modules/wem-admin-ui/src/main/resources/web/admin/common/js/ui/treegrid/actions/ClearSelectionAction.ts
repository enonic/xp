module api.ui.treegrid.actions {

    import Action = api.ui.Action;
    import Grid = api.ui.grid.Grid;

    export class ClearSelectionAction extends Action {

        constructor(grid: Grid<TreeNode<Object>>) {
            var selectionCount = grid.getSelectedRows().length;
            super("Clear Selection (" + selectionCount + ")");
            this.setEnabled(true);
            this.onExecuted(() => {
                grid.clearSelection();
            });
        }
    }
}
