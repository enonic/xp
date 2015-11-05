module api.system {

    export class StatusRequest extends api.rest.ResourceRequest<StatusJson, StatusResult> {

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getRestPath(), "status");
        }

        getParams(): Object {
            return {"cb": Date.now()}; //adding cache breaker for ie
        }

        sendAndParse(): wemQ.Promise<StatusResult> {

            return this.send().then((response: api.rest.JsonResponse<StatusJson>) => {
                return new StatusResult(response.getResult());
            });
        }
    }
}
