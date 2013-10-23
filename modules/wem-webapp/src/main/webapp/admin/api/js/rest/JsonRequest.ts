module api_rest {

    export class JsonRequest<T> {

        private path:Path;

        private method:string = "GET";

        private params:Object;

        private async:boolean = false;

        private successCallback:(response:JsonResponse) => void = null;

        private errorCallback:(requestError:RequestError) => void = null;

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

        setAsync(successCallback:(response:JsonResponse<T>) => void, errorCallback?:(requestError:RequestError) => void):JsonRequest<T> {
            this.async = true;
            this.successCallback = successCallback;
            this.errorCallback = errorCallback;
            return this;
        }

        private prepareGETRequest():XMLHttpRequest {
            var request = new XMLHttpRequest();
            var paramString = this.serializeParams(this.params);
            request.open(this.method, this.path.toString() + "?" + paramString, this.async);
            request.setRequestHeader("Accept", "application/json");
            return request;
        }

        private preparePOSTRequest():XMLHttpRequest {
            var request = new XMLHttpRequest();
            var paramString = JSON.stringify(this.params);
            request.open(this.method, this.path.toString(), this.async);
            request.setRequestHeader("Accept", "application/json");
            request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            request.send(paramString);
            return request;
        }

        send() {

            var request:XMLHttpRequest;
            if ("POST" == this.method.toUpperCase()) {
                request = this.preparePOSTRequest();
            }
            else {
                var request = this.prepareGETRequest();
                request.send();
            }

            if (this.async) {
                request.onreadystatechange = () => {

                    if (request.readyState == 4) {
                        if (request.status >= 200 && request.status < 300) {
                            if (this.successCallback) {
                                this.successCallback(new JsonResponse(request.response));
                            }
                        }
                        else {
                            var message:string = "HTTP Status " + request.status + " - " + request.statusText;
                            api_notify.showError(message);

                            if (this.errorCallback) {
                                this.errorCallback(new RequestError(request.statusText, request.responseText));
                            }
                        }
                    }
                };
            }
            else {
                this.jsonResponse = JSON.parse(request.response);
            }
        }

        getJsonResponse():JsonResponse<T> {
            return this.jsonResponse;
        }

        private serializeParams(params:Object):string {
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
