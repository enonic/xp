module app.browse {

    export class ModuleTreeGridContextMenu extends api.ui.menu.ContextMenu {

        constructor() {
            super();
        }

        setActions(actions:ModuleBrowseActions) {
            this.addAction(actions.DELETE_MODULE);
        }
    }
}
