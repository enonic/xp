module api_rest {

    export class ResourceRequest {

        private restPath:Path;

        private method:string = "GET";

        constructor() {
            this.restPath = Path.fromString( api_util.getAbsoluteUri("admin/rest") );
        }

        setMethod(value:string) {
            this.method = value;
        }

        getRestPath():Path {
            return this.restPath;
        }

        getRequestPath():Path {
            throw new Error("Must be implemented by inheritors");
        }

        getParams():Object {
            throw new Error("Must be implemented by inheritors");
        }

        send():JQueryPromise<Response>{

            var deferred = jQuery.Deferred<Response>();

            var jsonRequest = new JsonRequest().
            setMethod(this.method).
            setParams(this.getParams()).
            setPath(this.getRequestPath());

            jsonRequest.setAsync((jsonResponse:JsonResponse) => {
                deferred.resolve(jsonResponse);
            }, (requestError:RequestError) => {
                deferred.fail(requestError);
            });
            jsonRequest.send();
            return deferred.promise();
        }
    }
}
