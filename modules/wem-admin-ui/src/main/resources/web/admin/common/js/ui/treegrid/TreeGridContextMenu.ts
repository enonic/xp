module api.ui.treegrid {

    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;

    export class TreeGridContextMenu extends api.ui.menu.ContextMenu {

        private actions: TreeGridActions;

        constructor(actions: TreeGridActions) {
            super();

            this.actions = actions;
            this.addActions(actions.getAllActions());
        }

        getActions(): TreeGridActions {
            return this.actions;
        }
    }
}
