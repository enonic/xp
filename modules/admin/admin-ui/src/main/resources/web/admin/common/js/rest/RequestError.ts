module api.rest {

    import ExceptionType = api.ExceptionType;

    export class RequestError extends api.Exception {

        private statusCode: number;

        constructor(statusCode: number, errorMsg: string) {
            var notifyMsg = (statusCode > 0) ? errorMsg : "Unable to connect to server";
            var type = (statusCode >= 400 && statusCode < 500) ? ExceptionType.WARNING : ExceptionType.ERROR;

            super(notifyMsg, type);

            this.statusCode = statusCode;
        }

        getStatusCode(): number {
            return this.statusCode;
        }

        isNotFound() {
            return this.statusCode === StatusCode.NOT_FOUND;
        }
    }
}
