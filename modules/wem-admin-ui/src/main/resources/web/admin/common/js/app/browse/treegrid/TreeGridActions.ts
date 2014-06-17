module api.app.browse.treegrid {

    import Action = api.ui.Action;
    import Grid = api.ui.grid.Grid;
    import Item = api.item.Item;

    export class BaseTreeGridAction extends Action {

        constructor(label: string, shortcut?: string) {
            super(label, shortcut);
        }
    }

    export class SelectAllAction extends BaseTreeGridAction {

        constructor(grid: Grid<Item>) {
            super("Select All");
            this.setEnabled(true);
            this.onExecuted(() => {
                grid.selectAll();
            });
        }
    }

    export class ClearSelectionAction extends BaseTreeGridAction {

        constructor(grid: Grid<Item>) {
            super("Clear Selection");
            this.setEnabled(true);
            this.onExecuted(() => {
                grid.clearSelection();
            });
        }
    }

    export class TreeGridActions {

        public SELECT_ALL: api.ui.Action;
        public CLEAR_SELECTION: api.ui.Action;

        private allActions: api.ui.Action[] = [];

        private static INSTANCE: TreeGridActions;

        static init(grid: Grid<Item>): TreeGridActions {
            new TreeGridActions(grid);
            return TreeGridActions.INSTANCE;
        }

        static get(): TreeGridActions {
            return TreeGridActions.INSTANCE;
        }

        constructor(grid: Grid<Item>) {
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
