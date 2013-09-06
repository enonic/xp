module api_rest {

    export class JsonResponse {

        private json:any;

        constructor(json:any) {
            this.json = json;
        }

        getJson():any {
            return this.json;
        }
    }
}
