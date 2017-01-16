module api.rest {

    export class ResourceRequest<RAW_JSON_TYPE, PARSED_TYPE> {

        private restPath: Path;

        private method: string = 'GET';

        private heavyOperation: boolean;

        private timeoutMillis: number;

        constructor() {
            this.restPath = Path.fromString(api.util.UriHelper.getRestUri(''));
        }

        setMethod(value: string) {
            this.method = value;
        }

        getRestPath(): Path {
            return this.restPath;
        }

        getRequestPath(): Path {
            throw new Error('Must be implemented by inheritors');
        }

        getParams(): Object {
            throw new Error('Must be implemented by inheritors');
        }

        setTimeout(timeoutMillis: number) {
            this.timeoutMillis = timeoutMillis;
        }

        setHeavyOperation(value: boolean) {
            this.heavyOperation = value;
        }

        validate() {
            // Override to ensure any validation of ResourceRequest before sending.
        }

        send(): wemQ.Promise<JsonResponse<RAW_JSON_TYPE>> {

            this.validate();

            let jsonRequest = new JsonRequest<RAW_JSON_TYPE>().
                setMethod(this.method).setParams(this.getParams()).setPath(this.getRequestPath()).setTimeout(
                !this.heavyOperation ? this.timeoutMillis : 0);
            return jsonRequest.send();
        }

        sendAndParse(): wemQ.Promise<PARSED_TYPE> {
            throw new Error('sendAndParse method was not implemented');
        }
    }
}
