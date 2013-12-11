module app_browse {

    export class TemplateBrowsePanel extends api_app_browse.BrowsePanel<app_browse.TemplateBrowseItem> {

        private browseActions:app_browse.TemplateBrowseActions;

        private templateTreeGridPanel: app_browse.TemplateTreeGridPanel;

        private toolbar:TemplateBrowseToolbar;

        constructor() {
            var treeGridContextMenu = new app_browse.TemplateTreeGridContextMenu();
            this.templateTreeGridPanel = components.gridPanel = new TemplateTreeGridPanel({
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
                treeGridPanel: this.templateTreeGridPanel,
                browseItemPanel: browseItemPanel,
                filterPanel: undefined
            });

            api_content_site_template.SiteTemplateDeletedEvent.on((event: api_content_site_template.SiteTemplateDeletedEvent) => {
                var siteTemplateKey = event.getSiteTemplateKey();
                this.templateTreeGridPanel.remove(siteTemplateKey.toString());
                console.log(siteTemplateKey);
            });

            this.templateTreeGridPanel.addListener(<api_app_browse_grid.TreeGridPanelListener>{
                onSelectionChanged: (event: api_app_browse_grid.TreeGridSelectionChangedEvent) => {
                    this.browseActions.updateActionsEnabledState(<any[]>event.selectedModels);
                }
            });

            api_content_site_template.SiteTemplateImportedEvent.on(() => {
                this.setRefreshNeeded(true);
                this.refreshFilterAndGrid();
            });
        }

        extModelsToBrowseItems(models: Ext_data_Model[]): api_app_browse.BrowseItem<api_content_site_template.SiteTemplateSummary>[] {

            var browseItems: api_app_browse.BrowseItem<api_content_site_template.SiteTemplateSummary>[] = [];

            models.forEach((model: Ext_data_Model, index: number) => {

                var siteTemplateSummary: api_content_site_template.SiteTemplateSummary = api_content_site_template.SiteTemplateSummary.fromExtModel(model);

                var item = new api_app_browse.BrowseItem<api_content_site_template.SiteTemplateSummary>(siteTemplateSummary).
                    setDisplayName(siteTemplateSummary.getDisplayName()).
                    setPath(siteTemplateSummary.getName()).
                    setIconUrl(api_util.getAdminUri('common/images/icons/icoMoon/32x32/folder.png'));

                browseItems.push(item);
            });
            return browseItems;
        }
    }

}