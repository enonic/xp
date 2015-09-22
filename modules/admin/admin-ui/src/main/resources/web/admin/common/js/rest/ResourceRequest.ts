module api.rest {

    export class ResourceRequest<RAW_JSON_TYPE, PARSED_TYPE> {

        private restPath: Path;

        private method: string = "GET";

        private timeoutMillis: number;

        constructor() {
            this.restPath = Path.fromString("admin/rest");
        }

        setMethod(value: string) {
            this.method = value;
        }

        getRestPath(): Path {
            return this.restPath;
        }

        getRequestPath(): Path {
            throw new Error("Must be implemented by inheritors");
        }

        getParams(): Object {
            throw new Error("Must be implemented by inheritors");
        }

        setTimeout(timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
        }

        /*
         * Override to ensure any validation of ResourceRequest before sending.
         */
        validate() {

        }

        send(): wemQ.Promise<JsonResponse<RAW_JSON_TYPE>> {

            this.validate();

            var jsonRequest = new JsonRequest<RAW_JSON_TYPE>().
                setMethod(this.method).
                setParams(this.getParams()).
                setPath(this.getRequestPath()).
                setTimeout(this.timeoutMillis);
            return jsonRequest.send();
        }

        sendAndParse(): wemQ.Promise<PARSED_TYPE> {
            throw new Error("sendAndParse method was not implemented");
        }
    }
}
