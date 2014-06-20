module api.app.browse.treegrid {

    import Action = api.ui.Action;
    import Grid = api.ui.grid.Grid;
    import Node = api.node.Node;

    export class SelectAllAction extends Action {

        constructor(grid: Grid<Node>) {
            super("Select All");
            this.setEnabled(true);
            this.onExecuted(() => {
                grid.selectAll();
            });
        }
    }
}
