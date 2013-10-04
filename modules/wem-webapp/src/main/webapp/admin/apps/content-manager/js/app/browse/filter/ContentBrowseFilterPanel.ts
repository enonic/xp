module app_browse_filter {

    export class ContentBrowseFilterPanel extends api_app_browse_filter.BrowseFilterPanel {

        constructor() {

            var contentTypeFacets = new api_facet.FacetGroupView("contentType", "Content Type");
            var spaceFacets = new api_facet.FacetGroupView("space", "Space");
            var lastModifiedFacets = new api_facet.FacetGroupView("lastModified", "Last modified", null, this.lastModifiedGroupFacetFilter);

            super(null, [contentTypeFacets, spaceFacets, lastModifiedFacets]);

            this.addListener(
                {
                    onReset: ()=> {

                        new api_content.FindContentRequest().send().done(
                            (jsonResponse:api_rest.JsonResponse) => {
                                this.updateFacets(api_facet.FacetFactory.createFacets(jsonResponse.getJson().facets));
                                new ContentBrowseResetEvent().fire();
                            }
                        );

                    },
                    onSearch: (values:{[s:string] : string[]; })=> {

                        var isClean = !this.hasFilterSet();
                        if (isClean) {
                            this.reset();
                            return;
                        }
                        //TODO: ranges are passed as separate facets because each of them is of type QueryFacet
                        new api_content.FindContentRequest(values['query'] ? values['query'][0] : undefined).
                            setContentTypes(values['contentType']).
                            setSpaces(values['space']).
                            setRanges(values).
                            setExpand(api_content.FindContentRequest.EXPAND_SUMMARY).
                            send().done((jsonResponse:api_rest.JsonResponse) => {
                                var response = jsonResponse.getJson();
                                this.updateFacets(api_facet.FacetFactory.createFacets(response.facets));
                                new ContentBrowseSearchEvent(response.contents).fire();
                            });

                    }
                }
            );
        }

        private lastModifiedGroupFacetFilter(facet:api_facet.Facet) {
            return facet instanceof api_facet.QueryFacet;
        }
    }

}
