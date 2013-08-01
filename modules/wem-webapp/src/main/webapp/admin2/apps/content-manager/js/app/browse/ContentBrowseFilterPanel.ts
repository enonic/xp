module app_browse {

    export class ContentBrowseFilterPanel extends api_app_browse.BrowseFilterPanel {


        constructor(facetData?:api_app_browse.FacetGroupData[]) {
            super(facetData);
            var searchAction = new api_app_browse.FilterSearchAction();
            searchAction.addExecutionListener((action:api_app_browse.FilterSearchAction)=> {
                var params = app_browse.createLoadContentParams(action.getFilterValues());
                api_remote.RemoteContentService.content_find(params, (response) => {
                    if (response && response.success) {
                        var ids = response.contents.map(function (item) {
                            return item.id
                        });
                        new ContentBrowseSearchEvent(ids).fire();
                        this.updateFacets(response.facets);
                    }
                });

            });
            var resetAction = new api_app_browse.FilterResetAction();
            resetAction.addExecutionListener((action:api_app_browse.FilterResetAction)=> {
                var params = app_browse.createLoadContentParams({});

                api_remote.RemoteContentService.content_find(params, (response) => {
                    if (response && response.success) {

                        // set facet data
                        this.updateFacets(response.facets);
                    }
                });
                new ContentBrowseResetEvent().fire();
            });
            this.setFilterSearchAction(searchAction);
            this.setFilterResetAction(resetAction);
        }
    }
}