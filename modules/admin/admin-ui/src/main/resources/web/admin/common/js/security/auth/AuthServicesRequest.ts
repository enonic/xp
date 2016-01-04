module api.security.auth {

    export class AuthServicesRequest extends AuthResourceRequest<AuthServiceJson[], AuthService[]> {

        constructor() {
            super();
            super.setMethod("GET");
        }

        getParams(): Object {
            return {};
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'services');
        }

        sendAndParse(): wemQ.Promise<AuthService[]> {

            return this.send().then((response: api.rest.JsonResponse<AuthServiceJson[]>) => {
                if (response.getResult()) {
                    return response.getResult().
                        map((authServiceJson) => {
                            return new AuthService(authServiceJson)
                        });
                }
                return null;
            });
        }

    }
}