module api_facet {

    export class TermsFacet extends Facet {

        private displayName:string;

        private entries:TermsFacetEntry[];

        constructor(name:string, displayName:string, entries:TermsFacetEntry[]) {
            super(name);
            this.displayName = displayName;
            this.entries = entries;
        }

        getTermsFacetEntries():TermsFacetEntry[] {
            return this.entries;
        }
    }
}