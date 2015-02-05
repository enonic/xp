module api.ui.treegrid {

    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;

    export class TreeGridContextMenu extends api.ui.menu.ContextMenu {

        private actions: TreeGridActions<any>;

        constructor(actions: TreeGridActions<any>) {
            super();

            this.actions = actions;
            this.addActions(actions.getAllActions());
        }

        getActions(): TreeGridActions<any> {
            return this.actions;
        }
    }
}
