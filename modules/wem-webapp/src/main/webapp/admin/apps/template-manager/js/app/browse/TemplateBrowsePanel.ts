module app_browse {

    export class TemplateBrowsePanel extends api_app_browse.BrowsePanel<app_browse.TemplateBrowseItem> {

        private browseActions:app_browse.TemplateBrowseActions;

        private toolbar:TemplateBrowseToolbar;

        constructor() {
            var treeGridContextMenu = new app_browse.TemplateTreeGridContextMenu();
            var treeGridPanel = components.gridPanel = new TemplateTreeGridPanel({
                contextMenu: treeGridContextMenu
            });

            this.browseActions = TemplateBrowseActions.get();
            treeGridContextMenu.setActions(this.browseActions);

            this.toolbar = new TemplateBrowseToolbar(this.browseActions);
            var browseItemPanel = components.detailPanel = new TemplateBrowseItemPanel({
                actionMenuActions: [
                    this.browseActions.NEW_TEMPLATE,
                    this.browseActions.EDIT_TEMPLATE,
                    this.browseActions.OPEN_TEMPLATE,
                    this.browseActions.DELETE_TEMPLATE,
                    this.browseActions.DUPLICATE_TEMPLATE,
                    this.browseActions.EXPORT_TEMPLATE
                ]
            });

            super({
                browseToolbar: this.toolbar,
                treeGridPanel: treeGridPanel,
                browseItemPanel: browseItemPanel,
                filterPanel: undefined
            });
        }

    }

}