module api.system {

    export class StatusRequest extends api.rest.ResourceRequest<StatusJson, any> {

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getRestPath(), "status");
        }

        getParams(): Object {
            return {};
        }
    }
}
