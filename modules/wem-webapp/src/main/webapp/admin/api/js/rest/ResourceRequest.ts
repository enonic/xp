module api_rest {

    export class ResourceRequest<T> {

        private restPath:Path;

        private method:string = "GET";

        constructor() {
            this.restPath = Path.fromString( api_util.getUri("admin/rest") );
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

            var jsonRequest = new JsonRequest<T>().
            setMethod(this.method).
            setParams(this.getParams()).
            setPath(this.getRequestPath());

            jsonRequest.setAsync((jsonResponse:JsonResponse<T>) => {
                deferred.resolve(jsonResponse);
            }, (requestError:RequestError) => {
                deferred.fail(requestError);
            });
            jsonRequest.send();
            return deferred.promise();
        }

        /*send2():ResponsePromise {

        }*/

    }
}
