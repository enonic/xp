module app.browse {

    export class TemplateTreeGridContextMenu extends api.ui.menu.ContextMenu {

        constructor() {
            super();
        }

        setActions(actions:app.browse.action.TemplateBrowseActions) {
            this.addAction(actions.NEW_TEMPLATE);
            this.addAction(actions.EDIT_TEMPLATE);
            this.addAction(actions.OPEN_TEMPLATE);
            this.addAction(actions.DELETE_TEMPLATE);
            this.addAction(actions.DUPLICATE_TEMPLATE);
            this.addAction(actions.EXPORT_TEMPLATE);
        }

    }

}