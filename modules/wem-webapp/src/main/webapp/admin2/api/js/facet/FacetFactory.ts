module api_facet {

    export class FacetFactory {

        public static createFacets(array:api_remote_content.ContentFacet[]):Facet[] {

            var facets:Facet[] = [];

            array.forEach((remoteFacet:api_remote_content.ContentFacet) => {

                if (remoteFacet._type == "terms") {
                    var facetEntries:TermsFacetEntry[] = [];

                    remoteFacet.terms.forEach((remoteTermsFacetEntry:any) => {
                        facetEntries.push(new TermsFacetEntry(remoteTermsFacetEntry.name, remoteTermsFacetEntry.displayName,
                            remoteTermsFacetEntry.count));
                    });

                    var termsFacet = new TermsFacet(remoteFacet.name, remoteFacet.displayName, facetEntries);
                    facets.push(termsFacet);
                }
                else if (remoteFacet._type == "query") {
                    var queryFacet = new QueryFacet(remoteFacet.name, remoteFacet.count);
                    facets.push(queryFacet);
                }
            });
            return facets;
        }
    }

}