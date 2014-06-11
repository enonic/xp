module app.browse.grid {

    import Action = api.ui.Action;
    import Grid = api.ui.grid.Grid;

    export class BaseContentGridAction extends Action {

        constructor(label: string, shortcut?: string) {
            super(label, shortcut);
        }
    }

    export class SelectAllAction extends BaseContentGridAction {

        constructor(grid: Grid<Slick.SlickData>) {
            super("Select All");
            this.setEnabled(true);
            this.onExecuted(() => {
                grid.selectAll();
            });
        }
    }

    export class ClearSelectionAction extends BaseContentGridAction {

        constructor(grid: Grid<Slick.SlickData>) {
            super("Clear Selection");
            this.setEnabled(true);
            this.onExecuted(() => {
                grid.clearSelection();
            });
        }
    }

    export class GridActions {

        public SELECT_ALL: api.ui.Action;
        public CLEAR_SELECTION: api.ui.Action;

        private allActions: api.ui.Action[] = [];

        private static INSTANCE: GridActions;

        static init(grid: Grid<Slick.SlickData>): GridActions {
            new GridActions(grid);
            return GridActions.INSTANCE;
        }

        static get(): GridActions {
            return GridActions.INSTANCE;
        }

        constructor(grid: Grid<Slick.SlickData>) {

            this.SELECT_ALL = new SelectAllAction(grid);
            this.CLEAR_SELECTION = new ClearSelectionAction(grid);

            this.allActions.push(this.SELECT_ALL, this.CLEAR_SELECTION);

            GridActions.INSTANCE = this;
        }

        getAllActions(): api.ui.Action[] {
            return this.allActions;
        }
    }
}
