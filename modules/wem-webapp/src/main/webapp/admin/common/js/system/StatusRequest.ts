module api.system {

    export class StatusRequest extends api.rest.ResourceRequest<StatusJson> {

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getRestPath(), "status");
        }

        getParams(): Object {
            return {};
        }
    }
}
