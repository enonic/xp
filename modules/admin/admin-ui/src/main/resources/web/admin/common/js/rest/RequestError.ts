module api.rest {

    import ExceptionType = api.ExceptionType;

    export class RequestError extends api.Exception {

        constructor(statusCode:number, statusText:string, errorMsg:string) {
            var notifyMsg = (statusCode > 0) ?
                                "HTTP Status " + statusCode + " - " + statusText + ": " + errorMsg : "Unable to connect to server";
            var type = (statusCode >= 400 && statusCode < 500) ? ExceptionType.WARNING : ExceptionType.ERROR;

            super(notifyMsg, type);
        }
    }
}
