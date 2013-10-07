module api_rest {

    export class RequestError extends Response {

        private statusText:string;

        private responseText:string;

        constructor(statusText:string, responseText:string) {
            super();
            this.statusText = statusText;
            this.responseText = responseText;
        }

        getStatusText() {
            return this.statusText;
        }

        getResponseText() {
            return this.responseText;
        }
    }
}
