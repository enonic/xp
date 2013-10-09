module app_browse_filter {

    export class ContentBrowseFilterPanel extends api_app_browse_filter.BrowseFilterPanel {

        constructor() {

            var contentTypeFacets = new api_facet.FacetGroupView("contentType", "Content Type");
            var spaceFacets = new api_facet.FacetGroupView("space", "Space");
            var lastModifiedFacets = new api_facet.FacetGroupView("lastModified", "Last modified", null, this.lastModifiedGroupFacetFilter);

            super(null, [contentTypeFacets, spaceFacets, lastModifiedFacets]);

            this.resetFacets(true);

            this.addListener(
                {
                    onReset: ()=> {
                        this.resetFacets();
                    },

                    onSearch: (values:{[s:string] : string[]; })=> {

                        var isClean = !this.hasFilterSet();
                        if (isClean) {
                            this.reset();
                            return;
                        }

                        // ranges are passed as separate facets because each of them is of type QueryFacet
                        // but should all go under one facet name, i.e values['ranges']
                        var ranges = this.extractRangesFromFilterValues(values);

                        new api_content.FindContentRequest(values['query'] ? values['query'][0] : undefined).
                            setContentTypes(values['contentType']).
                            setSpaces(values['space']).
                            setRanges(ranges).
                            setExpand(api_content.FindContentRequest.EXPAND_SUMMARY).
                            send().done((jsonResponse:api_rest.JsonResponse) => {
                                var response = jsonResponse.getJson();
                                this.updateFacets(api_facet.FacetFactory.createFacets(response.facets));
                                new ContentBrowseSearchEvent(response.contents).fire();
                            })
                        ;

                    }
                }
            );
        }

        private resetFacets(supressEvent?:boolean) {
            new api_content.FindContentRequest().setCount(0).send().done(
                (jsonResponse:api_rest.JsonResponse) => {
                    var termsFacets:api_facet.Facet[] = api_facet.FacetFactory.createFacets(jsonResponse.getJson().facets);
                    this.updateFacets(termsFacets);
                    if (!supressEvent) {
                        new ContentBrowseResetEvent().fire();
                    }
                }
            );
        }

        private extractRangesFromFilterValues(values:{ [s : string ] : string[]; }):{lower:Date; upper:Date}[] {
            var ranges = [];

            if (values) {
                var now = new Date();
                var oneDayAgo = new Date();
                var oneWeekAgo = new Date();
                var oneHourAgo = new Date();
                oneDayAgo.setDate(now.getDate() - 1);
                oneWeekAgo.setDate(now.getDate() - 7);
                Admin.lib.DateHelper.addHours(oneHourAgo, -1);

                for (var prop in values) {
                    if (values.hasOwnProperty(prop) && values[prop].length > 0) {
                        var lower = null;

                        switch (values[prop][0]) {
                        case '< 1 day':
                            lower = oneDayAgo;
                            break;
                        case '< 1 hour':
                            lower = oneHourAgo;
                            break;
                        case '1 < week':
                            lower = oneWeekAgo;
                            break;
                        }

                        if (lower) {
                            ranges.push({
                                lower: lower,
                                upper: null
                            })
                        }
                    }
                }
            }
            return ranges;
        }

        private lastModifiedGroupFacetFilter(facet:api_facet.Facet) {
            return facet instanceof api_facet.QueryFacet;
        }
    }

}
