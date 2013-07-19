module app_browse {

    export class SpaceBrowsePanel extends api_app_browse.BrowsePanel {

        private toolbar:SpaceBrowseToolbar;

        private filterPanel:api_app_browse.BrowseFilterPanel;

        private grid:SpaceTreeGridPanel;

        private browseItemPanel:app_browse.SpaceBrowseItemPanel;

        constructor() {

            this.toolbar = new app_browse.SpaceBrowseToolbar();
            this.grid = components.gridPanel = new SpaceTreeGridPanel('spaceTreeGrid');
            this.browseItemPanel = components.detailPanel = new app_browse.SpaceBrowseItemPanel();

            this.filterPanel = new app_browse.SpaceBrowseFilterPanel();
            var action = new api_app_browse.FilterSearchAction();
            action.addExecutionListener((action:api_app_browse.FilterSearchAction)=> {
                console.log(action.getFilterValues());
            });
            this.filterPanel.setFilterSearchAction(action);

            super(this.toolbar, this.grid, this.browseItemPanel, this.filterPanel);

            GridSelectionChangeEvent.on((event) => {

                if (event.getModels().length == 0) {
                    this.browseItemPanel.setItems([]);
                }
                else {
                    var models:api_model.SpaceModel[] = event.getModels();
                    var spaceLoader:SpaceLoader = new SpaceLoader(SpaceLoader.convert(models));
                    spaceLoader.load((loadedSpaces:api_remote.SpaceSummary[]) => {

                        var items:api_app_browse.BrowseItem[] = [];
                        loadedSpaces.forEach((space:api_remote.SpaceSummary, index:number) => {
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

    export class SpaceLoader {

        private getCalls:api_remote.RemoteCallSpaceGetParams[] = [];

        private spaces:api_remote.SpaceSummary[] = [];

        static convert(models:api_model.SpaceModel[]):api_remote.RemoteCallSpaceGetParams[] {
            var getParams:api_remote.RemoteCallSpaceGetParams[] = [];
            models.forEach((model:api_model.SpaceModel)=> {
                getParams.push({
                    "spaceName": [model.data.name]
                });
            });
            return getParams;
        }

        constructor(getCalls:api_remote.RemoteCallSpaceGetParams[]) {
            this.getCalls = getCalls;
        }

        load(callback:(loadedSpaces:api_remote.SpaceSummary[])=>void) {
            this.getCalls.forEach((getParams:api_remote.RemoteCallSpaceGetParams)=> {
                api_remote.RemoteService.space_get(getParams, (result:api_remote.RemoteCallSpaceGetResult) => {
                    if (result) {
                        this.spaces.push(result.space);
                        if (this.spaces.length >= this.getCalls.length) {
                            callback(this.spaces);
                        }
                    }
                });
            });
        }
    }
}
