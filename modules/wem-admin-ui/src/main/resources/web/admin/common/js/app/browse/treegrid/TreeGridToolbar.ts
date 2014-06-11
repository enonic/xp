module api.app.browse.treegrid {

    export class TreeGridToolbar extends api.ui.toolbar.Toolbar {

        constructor(actions: TreeGridActions) {
            super();

            this.addActions(actions.getAllActions());
        }
    }
}
