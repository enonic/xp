module api.rest {

    export class JsonResponse<RAW_JSON_TYPE> extends api.rest.Response {

        private json:any;

        constructor(json:any) {
            super();
            try {
                this.json = JSON.parse(json);
            } catch (e) {
                console.warn("Failed to parse the response", json, e);
            }
        }

        isBlank():boolean {
            return !this.json;
        }

        getJson():any {
            return this.json;
        }

        hasResult():boolean {
            if( this.json == undefined || this.json == null ) {
                return false;
            }
            return true;
        }

        getResult():RAW_JSON_TYPE {
            if( !this.hasResult() ) {
                return null;
            }

            if( this.json.result ) {
                return <RAW_JSON_TYPE>this.json.result;
            }
            else {
                return <RAW_JSON_TYPE>this.json;
            }
        }
    }
}
