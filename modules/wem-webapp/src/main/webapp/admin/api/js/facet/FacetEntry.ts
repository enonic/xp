module api_facet {

    export class FacetEntry {

        private name:string;

        constructor(name:string) {
            this.name = name;
        }

        getName():string {
            return this.name;
        }
    }
}