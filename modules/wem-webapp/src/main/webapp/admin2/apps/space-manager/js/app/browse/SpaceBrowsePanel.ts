module app_browse {

    export class SpaceBrowsePanel extends api_app_browse.BrowsePanel {

        private browseActions:app_browse.SpaceBrowseActions;

        private toolbar:SpaceBrowseToolbar;

        private treeGridPanel:SpaceTreeGridPanel;

        private filterPanel:app_browse_filter.SpaceBrowseFilterPanel;

        private browseItemPanel:SpaceBrowseItemPanel;

        constructor() {

            var treeGridContextMenu = new SpaceTreeGridContextMenu();
            this.treeGridPanel = components.gridPanel = new SpaceTreeGridPanel({
                contextMenu: treeGridContextMenu
            });

            this.browseActions = SpaceBrowseActions.init(this.treeGridPanel);
            treeGridContextMenu.setActions(this.browseActions);

            this.toolbar = new app_browse.SpaceBrowseToolbar(this.browseActions);

            this.browseItemPanel = components.detailPanel =
                                   new SpaceBrowseItemPanel({ actionMenuActions: [
                                       this.browseActions.EDIT_SPACE,
                                       this.browseActions.OPEN_SPACE,
                                       this.browseActions.DELETE_SPACE]});

            this.filterPanel = new app_browse_filter.SpaceBrowseFilterPanel();

            super({
                browseToolbar: this.toolbar,
                treeGridPanel: this.treeGridPanel,
                browseItemPanel: this.browseItemPanel,
                filterPanel: this.filterPanel});

            this.treeGridPanel.addListener(<api_app_browse_grid.TreeGridPanelListener>{
                onSelectionChanged: (event:api_app_browse_grid.TreeGridSelectionChangedEvent) => {
                    this.browseActions.updateActionsEnabledState(<any[]>event.selectedModels);
                }
            });
        }

        extModelsToBrowseItems(models:api_model.SpaceExtModel[]) {

            var browseItems:api_app_browse.BrowseItem[] = [];
            models.forEach((model:api_model.SpaceExtModel, index:number) => {
                var item = new api_app_browse.BrowseItem(models[index]).
                    setDisplayName(model.data.displayName).
                    setPath(model.data.name).
                    setIconUrl(model.data.iconUrl);
                browseItems.push(item);
            });
            return browseItems;
        }
    }
}
