module api_facet {

    export interface TermsFacet extends Facet {
        name:string;
        displayName:string;
        terms:TermsFacetEntry[];
    }
}