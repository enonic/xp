module api_rest {

    export class JsonRequest<T> {

        private path:Path;

        private method:string = "GET";

        private params:Object;

        private jsonResponse:JsonResponse<T>;

        setPath(value:Path):JsonRequest<T> {
            this.path = value;
            return this;
        }

        setMethod(value:string):JsonRequest<T> {
            this.method = value;
            return this;
        }

        setParams(params:Object):JsonRequest<T> {
            this.params = params;
            return this;
        }

        private prepareGETRequest(request:XMLHttpRequest) {
            var paramString = JsonRequest.serializeParams(this.params);
            request.open(this.method, this.path.toString() + "?" + paramString, true);
            request.setRequestHeader("Accept", "application/json");
            return request;
        }

        private preparePOSTRequest(request:XMLHttpRequest) {
            var paramString = JSON.stringify(this.params);
            request.open(this.method, this.path.toString(), true);
            request.setRequestHeader("Accept", "application/json");
            request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            request.send(paramString);
            return request;
        }

        send():JQueryPromise<Response> {

            var deferred:JQueryDeferred<Response> = jQuery.Deferred<Response>();
            var request:XMLHttpRequest = new XMLHttpRequest();
            request.onreadystatechange = () => {

                if (request.readyState == 4) {
                    if (request.status >= 200 && request.status < 300) {
                        deferred.resolve(new JsonResponse(request.response));
                    }
                    else {
                        var message:string = "HTTP Status " + request.status + " - " + request.statusText;
                        api_notify.showError(message);

                        deferred.fail(new RequestError(request.statusText, request.responseText));
                    }
                }
            };

            if ("POST" == this.method.toUpperCase()) {
                request = this.preparePOSTRequest(request);
            }
            else {
                var request = this.prepareGETRequest(request);
                request.send();
            }

            return deferred.promise();
        }

        deferredSend():JQueryDeferred<Response> {

            var deferred:JQueryDeferred<Response> = jQuery.Deferred<Response>();

            var request:XMLHttpRequest = new XMLHttpRequest();
            request.onreadystatechange = () => {

                if (request.readyState == 4) {
                    if (request.status >= 200 && request.status < 300) {
                        deferred.resolve(new JsonResponse(request.response));
                    }
                    else {
                        var message:string = "HTTP Status " + request.status + " - " + request.statusText;
                        api_notify.showError(message);

                        deferred.fail(new RequestError(request.statusText, request.responseText));
                    }
                }
            };

            if ("POST" == this.method.toUpperCase()) {
                request = this.preparePOSTRequest(request);
            }
            else {
                var request = this.prepareGETRequest(request);
                request.send();
            }

            return deferred;
        }

        private static serializeParams(params:Object):string {
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
