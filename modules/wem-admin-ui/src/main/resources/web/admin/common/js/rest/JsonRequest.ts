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

        send(): Q.Promise<Response> {

            var deferred = Q.defer<Response>();

            var request: XMLHttpRequest = new XMLHttpRequest();
            request.timeout = 10000;
            request.onreadystatechange = () => {

                if (request.readyState == 4) {
                    if (request.status >= 200 && request.status < 300) {
                        deferred.resolve(new JsonResponse(request.response));
                    } else {
                        var errorJson: any = request.response ? JSON.parse(request.response) : null;
                        var errorMsg = errorJson ? errorJson.message : "";
                        var notifyMessage: string = "HTTP Status " + request.status + " - " + request.statusText + ": " + errorMsg;

                        if (request.status <= 0) {
                            api.notify.showError("Unable to connect to server");
                        } else if (request.status >= 400 && request.status < 500) {
                            api.notify.showWarning(notifyMessage);
                        } else {
                            api.notify.showError(notifyMessage);
                        }


                        deferred.reject(new RequestError(request.status, request.statusText, request.responseText, errorMsg));
                    }
                }
            };

            if ("POST" == this.method.toUpperCase()) {
                this.preparePOSTRequest(request);
                var paramString = JSON.stringify(this.params);
                request.send(paramString);
            }
            else {
                var request = this.prepareGETRequest(request);
                request.send();
            }

            return deferred.promise;
        }

        private prepareGETRequest(request: XMLHttpRequest) {
            var paramString = JsonRequest.serializeParams(this.params);
            request.open(this.method, api.util.getUri(this.path.toString()) + "?" + paramString, true);
            request.setRequestHeader("Accept", "application/json");
            return request;
        }

        private preparePOSTRequest(request: XMLHttpRequest) {
            request.open(this.method, api.util.getUri(this.path.toString()), true);
            request.setRequestHeader("Accept", "application/json");
            request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
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
