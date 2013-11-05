module app_browse {

    export class SpaceBrowsePanel extends api_app_browse.BrowsePanel<api_model.SpaceExtModel> {

        private browseActions:app_browse.SpaceBrowseActions;

        private toolbar:SpaceBrowseToolbar;


        constructor() {

            var treeGridContextMenu = new SpaceTreeGridContextMenu();
            var treeGridPanel = components.gridPanel = new SpaceTreeGridPanel({
                contextMenu: treeGridContextMenu
            });

            this.browseActions = SpaceBrowseActions.init(treeGridPanel);
            treeGridContextMenu.setActions(this.browseActions);

            this.toolbar = new app_browse.SpaceBrowseToolbar(this.browseActions);

            var browseItemPanel = components.detailPanel =
                                   new SpaceBrowseItemPanel({ actionMenuActions: [
                                       this.browseActions.EDIT_SPACE,
                                       this.browseActions.OPEN_SPACE,
                                       this.browseActions.DELETE_SPACE]});

            var filterPanel = new app_browse_filter.SpaceBrowseFilterPanel();

            super({
                browseToolbar: this.toolbar,
                treeGridPanel: treeGridPanel,
                browseItemPanel: browseItemPanel,
                filterPanel: filterPanel});

            treeGridPanel.addListener(<api_app_browse_grid.TreeGridPanelListener>{
                onSelectionChanged: (event:api_app_browse_grid.TreeGridSelectionChangedEvent) => {
                    this.browseActions.updateActionsEnabledState(<any[]>event.selectedModels);
                }
            });

            app_wizard.SpaceCreatedEvent.on((event) => {
                this.setRefreshNeeded(true);
            });

            app_wizard.SpaceUpdatedEvent.on((event) => {
                this.setRefreshNeeded(true);
            });
        }

        extModelsToBrowseItems(models:api_model.SpaceExtModel[]) {

            var browseItems:api_app_browse.BrowseItem<api_model.SpaceExtModel>[] = [];
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
