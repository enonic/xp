module app.browse.filter {

    export class ContentBrowseFilterPanel extends api.app.browse.filter.BrowseFilterPanel {

        constructor() {

            var contentTypeFacets = new api.facet.FacetGroupView("contentType", "Content Type");
            var spaceFacets = new api.facet.FacetGroupView("space", "Space");
            var lastModifiedFacets = new api.facet.FacetGroupView("lastModified", "Last modified", null, this.lastModifiedGroupFacetFilter);

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

                        new api.content.FindContentRequest<api.content.FindContentResult<api.content.json.ContentSummaryJson>>(values['query'] ? values['query'][0] : undefined).
                            setContentTypes(values['contentType']).
                            setSpaces(values['space']).
                            setRanges(ranges).
                            setExpand(api.content.FindContentRequest.EXPAND_SUMMARY).
                            send().done((jsonResponse:api.rest.JsonResponse<api.content.FindContentResult<api.content.json.ContentSummaryJson>>) => {
                                var result = jsonResponse.getResult();
                                this.updateFacets(api.facet.FacetFactory.createFacets(result.facets));
                                new ContentBrowseSearchEvent(result.contents).fire();
                            })
                        ;

                    }
                }
            );
        }

        private resetFacets(supressEvent?:boolean) {
            new api.content.FindContentRequest<api.content.FindContentResult<api.content.json.ContentSummaryJson>>().setCount(0).send().done(
                (jsonResponse:api.rest.JsonResponse<api.content.FindContentResult<api.content.json.ContentSummaryJson>>) => {
                    var termsFacets:api.facet.Facet[] = api.facet.FacetFactory.createFacets(jsonResponse.getResult().facets);
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

        private lastModifiedGroupFacetFilter(facet:api.facet.Facet) {
            return facet instanceof api.facet.QueryFacet;
        }
    }

}
