module api.security {

    import Role = api.security.Role;
    import Group = api.security.Group;
    import User = api.security.User;

    export class SecurityResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        private resourcePath: api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "security");
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToPrincipal(json: api.security.PrincipalJson): Principal {
            var pKey: PrincipalKey = PrincipalKey.fromString(json.key);
            if (pKey.isRole()) {
                return Role.fromJson(<api.security.RoleJson>json)

            } else if (pKey.isGroup()) {
                return Group.fromJson(<api.security.GroupJson>json);

            } else if (pKey.isUser()) {
                return User.fromJson(<api.security.UserJson>json);
            }
        }
    }
}