module api_rest {

    export class JsonResponse extends Response {

        private json:any;

        constructor(json:any) {
            super();
            try {
                this.json = JSON.parse(json);
            } catch (e) {
            }
        }

        getJson():any {
            return this.json;
        }
    }
}
