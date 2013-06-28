module app_browse {

    export class ContentAppBrowsePanel extends api_app_browse.AppBrowsePanel {

        constructor() {

            var toolbar = new ContentBrowseToolbar();
            var grid = components.gridPanel = new ContentTreeGridPanel('contentTreeGrid');
            var detail = components.detailPanel = new ContentDetailPanel();

            var filterPanel = new Admin.view.contentManager.FilterPanel({
                region: 'west',
                width: 200
            });

            super(toolbar, grid, detail, filterPanel);
        }
    }
}
