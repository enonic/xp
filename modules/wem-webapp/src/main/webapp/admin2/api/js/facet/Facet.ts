module api_facet {

    export class Facet {

        private name:string;

        constructor(name:string) {
            this.name = name;
        }

        getName():string {
            return this.name;
        }
    }
}