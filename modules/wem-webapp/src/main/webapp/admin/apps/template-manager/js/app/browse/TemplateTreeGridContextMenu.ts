module app_browse {

    export class TemplateTreeGridContextMenu extends api_ui_menu.ContextMenu {

        constructor() {
            super();
        }

        setActions(actions:TemplateBrowseActions) {
            this.addAction(actions.NEW_TEMPLATE);
            this.addAction(actions.EDIT_TEMPLATE);
            this.addAction(actions.OPEN_TEMPLATE);
            this.addAction(actions.DELETE_TEMPLATE);
            this.addAction(actions.DUPLICATE_TEMPLATE);
            this.addAction(actions.EXPORT_TEMPLATE);
        }

    }

}