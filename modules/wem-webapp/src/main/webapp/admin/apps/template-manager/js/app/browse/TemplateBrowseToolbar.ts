module app_browse {

    export class TemplateBrowseToolbar extends api_ui_toolbar.Toolbar {

        constructor(actions:TemplateBrowseActions) {
            super();

            this.addAction(actions.NEW_TEMPLATE);
            this.addAction(actions.EDIT_TEMPLATE);
            this.addAction(actions.OPEN_TEMPLATE);
            this.addAction(actions.DELETE_TEMPLATE);
            this.addAction(actions.DUPLICATE_TEMPLATE);
            this.addAction(actions.EXPORT_TEMPLATE);
        }

    }

}