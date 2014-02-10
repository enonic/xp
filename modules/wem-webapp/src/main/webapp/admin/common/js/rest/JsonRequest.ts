module api.rest {

    export class JsonRequest<T> {

        private path: Path;

        private method: string = "GET";

        private params: Object;

        private jsonResponse: JsonResponse<T>;

        setPath(value: Path): JsonRequest<T> {
            this.path = value;
            return this;
        }

        setMethod(value: string): JsonRequest<T> {
            this.method = value;
            return this;
        }

        setParams(params: Object): JsonRequest<T> {
            this.params = params;
            return this;
        }

        private prepareGETRequest(request: XMLHttpRequest) {
            var paramString = JsonRequest.serializeParams(this.params);
            request.open(this.method, api.util.getUri(this.path.toString()) + "?" + paramString, true);
            request.setRequestHeader("Accept", "application/json");
            return request;
        }

        private preparePOSTRequest(request: XMLHttpRequest) {
            var paramString = JSON.stringify(this.params);
            request.open(this.method, api.util.getUri(this.path.toString()), true);
            request.setRequestHeader("Accept", "application/json");
            request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            request.send(paramString);
            return request;
        }

        send(): JQueryPromise<Response> {

            var deferred: JQueryDeferred<Response> = jQuery.Deferred<Response>();
            var request: XMLHttpRequest = new XMLHttpRequest();
            request.timeout = 10000;
            request.onreadystatechange = () => {

                if (request.readyState == 4) {
                    if (request.status >= 200 && request.status < 300) {
                        deferred.resolve(new JsonResponse(request.response));
                    }
                    else {
                        var errorJson: any = JSON.parse(request.response);
                        var notifyMessage: string = "HTTP Status " + request.status + " - " + request.statusText + ": " + errorJson.message;

                        if (request.status >= 400 && request.status < 500) {
                            api.notify.showWarning(notifyMessage);
                        }
                        else {
                            api.notify.showError(notifyMessage);
                        }


                        deferred.reject(new RequestError(request.status, request.statusText, request.responseText, errorJson.message));
                    }
                }
            };

            try {
                if ("POST" == this.method.toUpperCase()) {
                    request = this.preparePOSTRequest(request);
                }
                else {
                    var request = this.prepareGETRequest(request);
                    request.send();
                }
            }
            catch (error) {
                var errorJson: any = JSON.parse(request.response);
                deferred.reject(new RequestError(request.status, request.statusText, request.responseText, errorJson.message));
            }

            return deferred.promise();
        }

        private static serializeParams(params: Object): string {
            var str = "";
            for (var key in params) {
                if (params.hasOwnProperty(key)) {
                    if (str.length > 0) {
                        str += "&";
                    }
                    str += key + "=" + params[key];
                }
            }
            return str;
        }
    }
}
