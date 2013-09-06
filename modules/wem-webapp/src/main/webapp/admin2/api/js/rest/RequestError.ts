module api_rest {

    export class RequestError extends Response {

        private statusText:string;

        constructor(statusText:string) {
            super();
            this.statusText = statusText;
        }

        getStatusText() {
            return this.statusText;
        }
    }
}
