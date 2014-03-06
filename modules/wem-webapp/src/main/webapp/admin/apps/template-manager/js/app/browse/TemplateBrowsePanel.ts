module app.browse {

    export class TemplateBrowsePanel extends api.app.browse.BrowsePanel<app.browse.TemplateBrowseItem> {

        private browseActions: app.browse.action.TemplateBrowseActions;

        private templateTreeGridPanel: app.browse.TemplateTreeGridPanel;

        private toolbar: TemplateBrowseToolbar;

        private pageTemplateIconUri: string;

        private siteTemplateIconUri: string;

        constructor() {
            this.pageTemplateIconUri = api.util.getAdminUri('common/images/icons/icoMoon/32x32/newspaper.png');
            this.siteTemplateIconUri = api.util.getAdminUri('common/images/icons/icoMoon/32x32/earth.png');

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

        extModelsToBrowseItems(models: Ext_data_Model[]): api.app.browse.BrowseItem<app.browse.TemplateSummary>[] {

            var browseItems: api.app.browse.BrowseItem<app.browse.TemplateSummary>[] = [];

            models.forEach((model: Ext_data_Model, index: number) => {

                var templateSummary: app.browse.TemplateSummary = app.browse.TemplateSummary.fromExtModel(model);

                var type: TemplateType = TemplateType[<string>model.get('templateType')];
                var iconUrl = type === TemplateType.PAGE ? this.pageTemplateIconUri : this.siteTemplateIconUri;

                var item = new api.app.browse.BrowseItem<app.browse.TemplateSummary>(templateSummary).
                    setDisplayName(templateSummary.getDisplayName()).
                    setPath(templateSummary.getName()).
                    setIconUrl(iconUrl);

                browseItems.push(item);
            });
            return browseItems;
        }
    }

}