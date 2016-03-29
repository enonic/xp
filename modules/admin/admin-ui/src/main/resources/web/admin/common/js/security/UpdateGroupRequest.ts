module api.security {

    export class UpdateGroupRequest extends SecurityResourceRequest<GroupJson, Group> {

        private key: PrincipalKey;
        private displayName: string;
        private membersToAdd: PrincipalKey[] = [];
        private membersToRemove: PrincipalKey[] = [];
        private description: string;

        constructor() {
            super();
            super.setMethod("POST");
        }

        setKey(key: PrincipalKey): UpdateGroupRequest {
            this.key = key;
            return this;
        }

        setDisplayName(displayName: string): UpdateGroupRequest {
            this.displayName = displayName;
            return this;
        }

        addMembers(members: PrincipalKey[]): UpdateGroupRequest {
            this.membersToAdd = members.slice(0);
            return this;
        }

        removeMembers(members: PrincipalKey[]): UpdateGroupRequest {
            this.membersToRemove = members.slice(0);
            return this;
        }

        setDescription(description: string): UpdateGroupRequest {
            this.description = description;
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
            return api.rest.Path.fromParent(super.getResourcePath(), 'principals', 'updateGroup');
        }

        sendAndParse(): wemQ.Promise<Group> {

            return this.send().then((response: api.rest.JsonResponse<GroupJson>) => {
                return Group.fromJson(response.getResult());
            });
        }

    }
}