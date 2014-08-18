module api.rest {

    export class JsonRequest<RAW_JSON_TYPE> {

        private path: Path;

        private method: string = "GET";

        private params: Object;

        setPath(value: Path): JsonRequest<RAW_JSON_TYPE> {
            this.path = value;
            return this;
        }

        setMethod(value: string): JsonRequest<RAW_JSON_TYPE> {
            this.method = value;
            return this;
        }

        setParams(params: Object): JsonRequest<RAW_JSON_TYPE> {
            this.params = params;
            return this;
        }

        send(): Q.Promise<JsonResponse<RAW_JSON_TYPE>> {

            var deferred = Q.defer<JsonResponse<RAW_JSON_TYPE>>();

            var request: XMLHttpRequest = new XMLHttpRequest();
            request.timeout = 10000;
            request.onreadystatechange = () => {

                if (request.readyState == 4) {

                    if (request.status >= 200 && request.status < 300) {
                        deferred.resolve(new JsonResponse<RAW_JSON_TYPE>(request.response));
                    } else {
                        try {
                            var errorJson: any = request.response ? JSON.parse(request.response) : null;
                        } catch (error) {
                            deferred.reject(error);
                        }

                        deferred.reject(new RequestError(request.status, request.statusText, errorJson ? errorJson.message : ""));
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
            var paramString = api.util.encodeUrlParams(this.params);
            request.open(this.method, api.util.getUri(this.path.toString()) + paramString, true);
            request.setRequestHeader("Accept", "application/json");
            return request;
        }

        private preparePOSTRequest(request: XMLHttpRequest) {
            request.open(this.method, api.util.getUri(this.path.toString()), true);
            request.setRequestHeader("Accept", "application/json");
            request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        }
    }
}
