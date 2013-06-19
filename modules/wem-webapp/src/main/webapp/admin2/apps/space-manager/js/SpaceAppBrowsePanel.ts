module app {

    export class SpaceAppBrowsePanel extends api.AppBrowsePanel {

        constructor() {

            var toolbar = new app_ui.BrowseToolbar();
            var grid = components.gridPanel = new app_ui.TreeGridPanel();
            var detail = components.detailPanel = new app_ui.SpaceDetailPanel();

            var filterPanel = new app_ui.FilterPanel({
                region: 'west',
                width: 200
            });

            super(toolbar, grid, detail, filterPanel);
        }
    }
}
