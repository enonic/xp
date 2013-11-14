module app_browse {

    export class ModuleBrowseToolbar extends api_ui_toolbar.Toolbar {

        constructor(actions:ModuleBrowseActions) {
            super();
            super.addAction(actions.IMPORT_MODULE);
            super.addAction(actions.EXPORT_MODULE);
            super.addAction(actions.DELETE_MODULE);
        }
    }
}