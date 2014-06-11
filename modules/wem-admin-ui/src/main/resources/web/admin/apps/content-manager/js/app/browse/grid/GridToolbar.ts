module app.browse.grid {

    export class GridToolbar extends api.ui.toolbar.Toolbar {

        constructor(actions: GridActions) {
            super();

            this.addAction(actions.SELECT_ALL);
            this.addAction(actions.CLEAR_SELECTION);
        }
    }
}
