module app_browse {

    export class SpaceAppBrowsePanel extends api_app_browse.AppBrowsePanel {

        private toolbar:SpaceBrowseToolbar;

        private filterPanel:app_ui.FilterPanel;

        private grid:SpaceTreeGridPanel;

        private detailPanel:SpaceDetailPanel;

        constructor() {

            this.toolbar = new app_browse.SpaceBrowseToolbar();
            this.grid = components.gridPanel = new SpaceTreeGridPanel('spaceTreeGrid');
            this.detailPanel = components.detailPanel = new SpaceDetailPanel();

            this.filterPanel = new app_ui.FilterPanel({
                region: 'west',
                width: 200
            });

            super(this.toolbar, this.grid, this.detailPanel, this.filterPanel);

            GridSelectionChangeEvent.on((event) => {

                var models:api_model.SpaceModel[] = event.getModels();
                var spaceLoader:SpaceLoader = new SpaceLoader(SpaceLoader.convert(models));
                spaceLoader.load((loadedSpaces:api_remote.SpaceSummary[]) => {

                    var items:api_app_browse.DetailPanelItem[] = [];
                    loadedSpaces.forEach( (space:api_remote.SpaceSummary, index:number) => {
                        var item = new api_app_browse.DetailPanelItem(models[index]).
                            setDisplayName(space.displayName).
                            setPath(space.name).
                            setIconUrl(space.iconUrl);
                        items.push(item);
                    });

                    this.detailPanel.setItems(items);
                });
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
