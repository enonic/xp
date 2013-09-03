module api_facet {

    export class TermsFacetEntry extends FacetEntry {

        private displayName:string;

        private count:number;

        constructor(name:string, displayName:string, count:number) {
            super(name);
            this.displayName = displayName;
            this.count = count;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getCount():number {
            return this.count;
        }
    }
}