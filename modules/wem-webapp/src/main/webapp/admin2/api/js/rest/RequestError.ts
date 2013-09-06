module api_rest {

    export class RequestError {

        private statusText:string;

        constructor(statusText:string) {
            this.statusText = statusText;
        }

        getStatusText() {
            return this.statusText;
        }
    }
}
