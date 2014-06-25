module app.browse {

    export class ModuleTreeGridContextMenu extends api.ui.menu.ContextMenu {

        constructor() {
            super();
        }

        setActions(actions:ModuleBrowseActions) {
            super.addAction(actions.START_MODULE);
            super.addAction(actions.STOP_MODULE);
            super.addAction(actions.UPDATE_MODULE);
            super.addAction(actions.UNINSTALL_MODULE);
        }
    }
}
