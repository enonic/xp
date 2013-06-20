module app_browse {

    export class SpaceAppBrowsePanel extends api_browse.AppBrowsePanel {

        constructor() {

            var toolbar = new BrowseToolbar();
            var grid = components.gridPanel = new app_ui.TreeGridPanel();
            var detail = components.detailPanel = new app_browse.SpaceDetailPanel();

            var filterPanel = new app_ui.FilterPanel({
                region: 'west',
                width: 200
            });

            super(toolbar, grid, detail, filterPanel);
        }
    }
}
