module api.app.browse.treegrid {

    import Action = api.ui.Action;
    import Grid = api.ui.grid.Grid;
    import Node = api.node.Node;

    export class ClearSelectionAction extends Action {

        constructor(grid: Grid<Node>) {
            super("Clear Selection");
            this.setEnabled(true);
            this.onExecuted(() => {
                grid.clearSelection();
            });
        }
    }
}
