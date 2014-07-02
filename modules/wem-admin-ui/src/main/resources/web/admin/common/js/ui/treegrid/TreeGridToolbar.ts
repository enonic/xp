module api.ui.treegrid {

    export class TreeGridToolbar extends api.ui.toolbar.Toolbar {

        constructor(actions: TreeGridActions) {
            super();

            this.addActions(actions.getAllActions());
        }
    }
}
