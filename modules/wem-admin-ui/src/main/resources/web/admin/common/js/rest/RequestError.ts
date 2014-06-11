module api.rest {

    import Type = api.notify.Type;

    export class RequestError extends api.notify.Exception {

        constructor(statusCode:number, statusText:string, errorMsg:string) {
            var notifyMsg = (statusCode > 0) ?
                                "HTTP Status " + statusCode + " - " + statusText + ": " + errorMsg : "Unable to connect to server";
            var type = (statusCode >= 400 && statusCode < 500) ? Type.WARNING : Type.ERROR;

            super(notifyMsg, type);
        }
    }
}
