module app_browse_filter {

    export class ContentBrowseFilterPanel extends api_app_browse_filter.BrowseFilterPanel {

        constructor(facetData?:api_app_browse_filter.FacetGroupData[]) {
            super(facetData);

            var searchAction = new api_app_browse_filter.FilterSearchAction();

            searchAction.addExecutionListener((action:api_app_browse_filter.FilterSearchAction)=> {
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

            this.setFilterSearchAction(searchAction);

            var resetAction = new api_app_browse_filter.FilterResetAction();

            resetAction.addExecutionListener((action:api_app_browse_filter.FilterResetAction)=> {
                var params = app_browse.createLoadContentParams({});

                api_remote.RemoteContentService.content_find(params, (response) => {
                    if (response && response.success) {

                        // set facet data
                        this.updateFacets(response.facets);
                    }
                }, (failure) => {
                    console.log(failure.error);
                });
                new ContentBrowseResetEvent().fire();
            });

            this.setFilterResetAction(resetAction);
        }
    }
}