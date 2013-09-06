module api_rest {

    export class ResourceRequest {

        private restUrl:string;

        private method:string = "GET";

        private async:boolean = false;

        private successCallback:(response:JsonResponse) => void = null;

        private errorCallback:(requestError:RequestError) => void = null;

        private jsonResponse:JsonResponse;

        constructor() {
            this.restUrl = api_util.getAbsoluteUri("admin/rest");
        }

        setMethod(value:string) {
            this.method = value;
        }

        getMethod():string {
            return this.method;
        }

        setAsync(successCallback:(response:JsonResponse) => void, errorCallback?:(requestError:RequestError) => void):ResourceRequest {
            this.async = true;
            this.successCallback = successCallback;
            this.errorCallback = errorCallback;
            return this;
        }

        getRestUrl() {
            return this.restUrl;
        }

        getUrl():string {
            throw new Error("Must be implemented by inheritors");
        }

        send() {
            var jsonRequest = new JsonRequest().
                setMethod(this.method).
                setUrl(this.getUrl());

            if (this.async) {
                jsonRequest.setAsync(this.successCallback, this.errorCallback);
                jsonRequest.send();
            }
            else {
                jsonRequest.send();
                this.jsonResponse = jsonRequest.getJsonResponse();
            }
        }

        getJsonResponse():JsonResponse {
            if (this.async) {
                throw new Error("Do not use this method when requesting asynchronously. JsonResponse is then sent as argument to successCallback");
            }
            return this.jsonResponse;
        }

    }
}
