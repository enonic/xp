module api.query.aggregation {

    export class Bucket {

        private name:string;
        private docCount:number;

        constructor( name:string, docCount:number )
        {
            this.name = name;
            this.docCount = docCount;
        }

        public getName():string
        {
            return this.name;
        }

        public getDocCount():number
        {
            return this.docCount;
        }
    }
}
