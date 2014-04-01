module api.rest {

    export class RequestError extends Response {

        private statusCode:number;

        private statusText:string;

        private responseText:string;

        private message:string;

        constructor(statusCode:number, statusText:string, responseText:string, message:string) {
            super();
            this.statusCode = statusCode;
            this.statusText = statusText;
            this.responseText = responseText;
            this.message = message;
        }

        getStatusCode() {
            return this.statusText;
        }

        getStatusText() {
            return this.statusText;
        }

        getResponseText() {
            return this.responseText;
        }

        getMessage() {
            return this.message;
        }
    }
}
