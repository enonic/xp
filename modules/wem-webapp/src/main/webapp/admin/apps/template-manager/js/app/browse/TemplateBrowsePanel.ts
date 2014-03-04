module app.browse {

    export class TemplateBrowsePanel extends api.app.browse.BrowsePanel<app.browse.TemplateBrowseItem> {

        private browseActions: app.browse.action.TemplateBrowseActions;

        private templateTreeGridPanel: app.browse.TemplateTreeGridPanel;

        private toolbar: TemplateBrowseToolbar;

        constructor() {
            var treeGridContextMenu = new app.browse.TemplateTreeGridContextMenu();
            this.templateTreeGridPanel = components.gridPanel = new TemplateTreeGridPanel({
                contextMenu: treeGridContextMenu
            });

            this.browseActions = app.browse.action.TemplateBrowseActions.get();
            treeGridContextMenu.setActions(this.browseActions);

            this.toolbar = new TemplateBrowseToolbar(this.browseActions);
            var browseItemPanel = components.detailPanel = new TemplateBrowseItemPanel();

            super({
                browseToolbar: this.toolbar,
                treeGridPanel: this.templateTreeGridPanel,
                browseItemPanel: browseItemPanel,
                filterPanel: undefined
            });

            api.content.site.template.SiteTemplateDeletedEvent.on((event: api.content.site.template.SiteTemplateDeletedEvent) => {
                var siteTemplateKey = event.getSiteTemplateKey();
                this.templateTreeGridPanel.remove(siteTemplateKey.toString());
                console.log(siteTemplateKey);
            });

            this.templateTreeGridPanel.onTreeGridSelectionChanged((event: api.app.browse.grid.TreeGridSelectionChangedEvent) => {
                this.browseActions.updateActionsEnabledState(<any[]>event.getSelectedModels());
            });

            api.content.site.template.SiteTemplateImportedEvent.on(() => {
                this.setRefreshNeeded(true);
                this.refreshFilterAndGrid();
            });
        }

        extModelsToBrowseItems(models: Ext_data_Model[]): api.app.browse.BrowseItem<api.content.site.template.SiteTemplateSummary>[] {

            var browseItems: api.app.browse.BrowseItem<api.content.site.template.SiteTemplateSummary>[] = [];

            models.forEach((model: Ext_data_Model, index: number) => {

                var siteTemplateSummary: api.content.site.template.SiteTemplateSummary = api.content.site.template.SiteTemplateSummary.fromExtModel(model);

                var item = new api.app.browse.BrowseItem<api.content.site.template.SiteTemplateSummary>(siteTemplateSummary).
                    setDisplayName(siteTemplateSummary.getDisplayName()).
                    setPath(siteTemplateSummary.getName()).
                    setIconUrl(api.util.getAdminUri('common/images/icons/icoMoon/32x32/earth.png'));

                browseItems.push(item);
            });
            return browseItems;
        }
    }

}