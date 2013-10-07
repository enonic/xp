module api_facet {

    export class QueryFacet extends Facet {

        private count:number;

        constructor(name:string, count:number) {
            super(name);
            this.count = count;
        }

        getCount():number {
            return this.count;
        }
    }
}