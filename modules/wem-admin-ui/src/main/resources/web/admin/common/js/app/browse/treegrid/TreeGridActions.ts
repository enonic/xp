module api.app.browse.treegrid {

    import Action = api.ui.Action;
    import Grid = api.ui.grid.Grid;
    import Node = api.node.Node;

    export class TreeGridActions {

        public SELECT_ALL: api.ui.Action;
        public CLEAR_SELECTION: api.ui.Action;

        private allActions: api.ui.Action[] = [];

        private static INSTANCE: TreeGridActions;

        static init(grid: Grid<TreeNode<Node>>): TreeGridActions {
            new TreeGridActions(grid);
            return TreeGridActions.INSTANCE;
        }

        static get(): TreeGridActions {
            return TreeGridActions.INSTANCE;
        }

        constructor(grid: Grid<TreeNode<Node>>) {
            this.SELECT_ALL = new SelectAllAction(grid);
            this.CLEAR_SELECTION = new ClearSelectionAction(grid);
            this.allActions.push(this.SELECT_ALL, this.CLEAR_SELECTION);

            TreeGridActions.INSTANCE = this;
        }

        getAllActions(): api.ui.Action[] {
            return this.allActions;
        }
    }
}
