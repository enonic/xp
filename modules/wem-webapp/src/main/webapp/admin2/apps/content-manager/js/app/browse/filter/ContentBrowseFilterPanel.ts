module app_browse_filter {

    export class ContentBrowseFilterPanel extends api_app_browse_filter.BrowseFilterPanel {

        constructor(facetData?:api_app_browse_filter.FacetGroupData[]) {
            super(facetData);

            this.addListener({onSearch: (values:any[])=> {
                var params = app_browse.createLoadContentParams(values);
                api_remote_content.RemoteContentService.content_find(params, (response:api_remote_content.FindResult) => {
                    var ids = response.contents.map(function (item) {
                        return item.id
                    });
                    new ContentBrowseSearchEvent(ids).fire();
                    this.updateFacets(response.facets);
                });
            }});


            this.addListener({ onReset: ()=> {
                var params = app_browse.createLoadContentParams({});

                api_remote_content.RemoteContentService.content_find(params, (response:api_remote_content.FindResult) => {
                    // set facet data
                    this.updateFacets(response.facets);
                });
                new ContentBrowseResetEvent().fire();
            }});

        }
    }
}