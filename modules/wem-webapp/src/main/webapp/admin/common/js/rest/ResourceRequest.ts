module api_rest {

    export class ResourceRequest<T> {

        private restPath:Path;

        private method:string = "GET";

        constructor() {
            this.restPath = Path.fromString( "admin/rest" );
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

            var jsonRequest = new JsonRequest<T>().
            setMethod(this.method).
            setParams(this.getParams()).
            setPath(this.getRequestPath());
            return jsonRequest.send();
        }

        deferredSend():JQueryDeferred<Response>{

            var jsonRequest = new JsonRequest<T>().
                setMethod(this.method).
                setParams(this.getParams()).
                setPath(this.getRequestPath());
            return jsonRequest.deferredSend();
        }

    }
}
