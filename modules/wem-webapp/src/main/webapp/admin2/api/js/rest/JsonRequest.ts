module api_rest {

    export class JsonRequest {

        private url:string;

        private method:string = "GET";

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

        setAsync(successCallback:(response:JsonResponse) => void, errorCallback?:(requestError:RequestError) => void):JsonRequest {
            this.async = true;
            this.successCallback = successCallback;
            this.errorCallback = errorCallback;
            return this;
        }

        send() {
            var request = new XMLHttpRequest();
            request.open(this.method, this.url, this.async);
            request.setRequestHeader("Accept", "application/json");
            request.send();

            if (this.async) {
                request.onload = () => {
                    if (this.successCallback != null) {
                        this.successCallback(new JsonResponse(JSON.parse(request.response)));
                    }
                };
                request.onerror = () => {
                    if (this.errorCallback != null) {
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
    }
}
