module api.security.auth {

    export class IsAuthenticatedRequest extends AuthResourceRequest<LoginResultJson, LoginResult> {

        constructor() {
            super();
            super.setMethod("GET");
        }

        getParams(): Object {
            return {};
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'authenticated');
        }

        sendAndParse(): wemQ.Promise<LoginResult> {

            return this.send().then((response: api.rest.JsonResponse<LoginResultJson>) => {
                return new LoginResult(response.getResult());
            });
        }

    }
}