module api.security {

    export class UpdateRoleRequest extends SecurityResourceRequest<RoleJson, Role> {

        private key: PrincipalKey;
        private displayName: string;
        private membersToAdd: PrincipalKey[] = [];
        private membersToRemove: PrincipalKey[] = [];
        private description: string;

        constructor() {
            super();
            super.setMethod("POST");
        }

        setKey(key: PrincipalKey): UpdateRoleRequest {
            this.key = key;
            return this;
        }

        setDisplayName(displayName: string): UpdateRoleRequest {
            this.displayName = displayName;
            return this;
        }

        setDescription(description: string): UpdateRoleRequest {
            this.description = description;
            return this;
        }

        addMembers(members: PrincipalKey[]): UpdateRoleRequest {
            this.membersToAdd = members.slice(0);
            return this;
        }

        removeMembers(members: PrincipalKey[]): UpdateRoleRequest {
            this.membersToRemove = members.slice(0);
            return this;
        }

        getParams(): Object {
            return {
                key: this.key.toString(),
                displayName: this.displayName,
                addMembers: this.membersToAdd.map((memberKey) => memberKey.toString()),
                removeMembers: this.membersToRemove.map((memberKey) => memberKey.toString()),
                description: this.description
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'principals', 'updateRole');
        }

        sendAndParse(): wemQ.Promise<Role> {

            return this.send().then((response: api.rest.JsonResponse<RoleJson>) => {
                return Role.fromJson(response.getResult());
            });
        }

    }
}