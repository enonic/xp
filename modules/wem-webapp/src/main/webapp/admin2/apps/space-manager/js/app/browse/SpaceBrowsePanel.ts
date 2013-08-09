module app_browse {

    export class SpaceBrowsePanel extends api_app_browse.BrowsePanel {

        private toolbar:SpaceBrowseToolbar;

        private filterPanel:app_browse_filter.SpaceBrowseFilterPanel;

        private grid:SpaceTreeGridPanel;

        private browseItemPanel:SpaceBrowseItemPanel;

        constructor() {

            this.toolbar = new app_browse.SpaceBrowseToolbar();
            this.grid = components.gridPanel = new SpaceTreeGridPanel('spaceTreeGrid');
            this.browseItemPanel = components.detailPanel = new app_browse.SpaceBrowseItemPanel();

            this.filterPanel = new app_browse_filter.SpaceBrowseFilterPanel();
            var action = new api_app_browse_filter.FilterSearchAction();
            action.addExecutionListener((action:api_app_browse_filter.FilterSearchAction)=> {
                console.log(action.getFilterValues());
            });
            this.filterPanel.setFilterSearchAction(action);

            super(this.toolbar, this.grid, this.browseItemPanel, this.filterPanel);

            GridSelectionChangeEvent.on((event) => {

                if (event.getModels().length == 0) {
                    this.browseItemPanel.setItems([]);
                }
                else {
                    var models:api_model.SpaceExtModel[] = event.getModels();
                    var spaceLoader:SpaceLoader = new SpaceLoader(SpaceLoader.convert(models));
                    spaceLoader.load((loadedSpaces:api_remote_space.SpaceSummary[]) => {

                        var items:api_app_browse.BrowseItem[] = [];
                        loadedSpaces.forEach((space:api_remote_space.SpaceSummary, index:number) => {
                            var item = new api_app_browse.BrowseItem(models[index]).
                                setDisplayName(space.displayName).
                                setPath(space.name).
                                setIconUrl(space.iconUrl);
                            items.push(item);
                        });

                        this.browseItemPanel.setItems(items);
                    });
                }

            });

            GridDeselectEvent.on((event) => {

                console.log("deselect", event);
            });
        }
    }

    class SpaceLoader {

        private getParams:api_remote_space.GetParams;

        static convert(models:api_model.SpaceExtModel[]):api_remote_space.GetParams {
            var spaceNames:string[] = models.map((model:api_model.SpaceExtModel) => {
                return model.data.name;
            });
            return {
                "spaceNames": spaceNames
            };
        }

        constructor(getParams:api_remote_space.GetParams) {
            this.getParams = getParams;
        }

        load(callback:(loadedSpaces:api_remote_space.SpaceSummary[])=>void) {
            api_remote.RemoteSpaceService.space_get(this.getParams, (result:api_remote_space.GetResult) => {
                callback(result.spaces);
            });
        }
    }
}
