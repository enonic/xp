module app.browse {

    export class TemplateBrowseToolbar extends api.ui.toolbar.Toolbar {

        constructor(actions:app.browse.action.TemplateBrowseActions) {
            super();

            this.addAction(actions.IMPORT_TEMPLATE);
            this.addAction(actions.NEW_TEMPLATE);
            this.addAction(actions.EDIT_TEMPLATE);
            this.addAction(actions.OPEN_TEMPLATE);
            this.addAction(actions.DELETE_TEMPLATE);
            this.addAction(actions.DUPLICATE_TEMPLATE);
            this.addAction(actions.EXPORT_TEMPLATE);
        }

    }

}