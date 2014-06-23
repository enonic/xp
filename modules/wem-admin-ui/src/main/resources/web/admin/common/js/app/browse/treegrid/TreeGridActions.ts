module api.app.browse.treegrid {

    import Action = api.ui.Action;
    import Grid = api.ui.grid.Grid;
    import Node = api.node.Node;

    export class TreeGridActions {

        public SELECT_ALL: api.ui.Action;
        public CLEAR_SELECTION: api.ui.Action;

        private allActions: api.ui.Action[] = [];

        constructor(grid: Grid<TreeNode<Node>>) {
            this.SELECT_ALL = new SelectAllAction(grid);
            this.CLEAR_SELECTION = new ClearSelectionAction(grid);
            this.allActions.push(this.SELECT_ALL, this.CLEAR_SELECTION);
        }

        getAllActions(): api.ui.Action[] {
            return this.allActions;
        }
    }
}
