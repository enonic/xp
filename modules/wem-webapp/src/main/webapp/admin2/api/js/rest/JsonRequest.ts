module api_rest {

    export class JsonRequest {

        private url:string;

        private method:string = "GET";

        private params:Object;

        private async:boolean = false;

        private successCallback:(response:JsonResponse) => void = null;

        private errorCallback:(requestError:RequestError) => void = null;

        private jsonResponse:JsonResponse;

        setUrl(value:string):JsonRequest {
            this.url = value;
            return this;
        }

        setMethod(value:string):JsonRequest {
            this.method = value;
            return this;
        }

        setParams(params:Object):JsonRequest {
            this.params = params;
            return this;
        }

        setAsync(successCallback:(response:JsonResponse) => void, errorCallback?:(requestError:RequestError) => void):JsonRequest {
            this.async = true;
            this.successCallback = successCallback;
            this.errorCallback = errorCallback;
            return this;
        }

        send() {
            var request = new XMLHttpRequest();
            var paramString;
            if ("POST" == this.method.toUpperCase()) {
                paramString = JSON.stringify(this.params);
                request.open(this.method, this.url, this.async);
                request.setRequestHeader("Accept", "application/json");
                request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
                request.setRequestHeader("Content-length", "" + paramString.length);
                request.setRequestHeader("Connection", "close");
                request.send(paramString);
            } else {
                paramString = this.serialize(this.params);
                request.open(this.method, this.url + "?" + paramString, this.async);
                request.setRequestHeader("Accept", "application/json");
                request.send();
            }

            if (this.async) {
                request.onload = () => {
                    if (this.successCallback) {
                        this.successCallback(new JsonResponse(request.response));
                    }
                };
                request.onerror = () => {
                    if (this.errorCallback) {
                        this.errorCallback(new RequestError(request.statusText));
                    }
                };
            }
            else {
                this.jsonResponse = JSON.parse(request.response);
            }
        }

        getJsonResponse():JsonResponse {
            return this.jsonResponse;
        }


        private serialize(obj:Object):string {
            var str = "";
            for (var key in obj) {
                if (obj.hasOwnProperty(key)) {
                    if (str.length > 0) {
                        str += "&";
                    }
                    str += key + "=" + obj[key];
                }
            }
            return str;
        }
    }
}
