module api_rest {

    export class JsonResponse extends Response {

        private json:any;

        constructor(json:any) {
            super();
            this.json = json;
        }

        getJson():any {
            return this.json;
        }
    }
}
