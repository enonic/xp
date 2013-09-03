module app_browse_filter {

    export class ContentBrowseFilterPanel extends api_app_browse_filter.BrowseFilterPanel {

        constructor() {

            var contentTypeFacets = new api_facet.FacetGroupView("contentType", "Content Type");
            var spaceFacets = new api_facet.FacetGroupView("space", "Space");
            var lastModifiedFacets = new api_facet.FacetGroupView("lastModified", "Last modified", null, this.lastModifiedGroupFacetFilter);

            super(null, [contentTypeFacets, spaceFacets, lastModifiedFacets]);

            this.addListener({ onReset: ()=> {

                console.log("ContentBrowseFilterPanel onReset");
                var params = app_browse.createLoadContentParams({});

                api_remote_content.RemoteContentService.content_find(params, (response:api_remote_content.FindResult) => {
                    // set facet data
                    this.updateFacets(api_facet.FacetFactory.createFacets(response.facets));
                });
                new ContentBrowseResetEvent().fire();
            }});

        }

        handleSearch(values:{[s:string] : string[]; }) {

            console.log("ContentBrowseFilterPanel handleSearch");
            var isClean = !this.hasFilterSet();
            if (isClean) {
                this.reset();
                return;
            }
            var params = app_browse.createLoadContentParams(values);
            api_remote_content.RemoteContentService.content_find(params, (response:api_remote_content.FindResult) => {
                var ids = response.contents.map(function (item) {
                    return item.id
                });
                new ContentBrowseSearchEvent(ids).fire();

                this.updateFacets(api_facet.FacetFactory.createFacets(response.facets));
            });
        }

        private lastModifiedGroupFacetFilter(facet:api_facet.Facet) {
            return facet instanceof api_facet.QueryFacet;
        }
    }

}
