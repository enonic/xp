module api.security {

    export class CreateGroupRequest extends SecurityResourceRequest<GroupJson, Group> {

        private key: PrincipalKey;
        private displayName: string;
        private members: PrincipalKey[] = [];
        private description: string;

        constructor() {
            super();
            super.setMethod("POST");
        }

        setKey(key: PrincipalKey): CreateGroupRequest {
            this.key = key;
            return this;
        }

        setDisplayName(displayName: string): CreateGroupRequest {
            this.displayName = displayName;
            return this;
        }

        setMembers(members: PrincipalKey[]): CreateGroupRequest {
            this.members = members.slice(0);
            return this;
        }

        setDescription(description: string): CreateGroupRequest {
            this.description = description;
            return this;
        }

        getParams(): Object {
            return {
                key: this.key.toString(),
                displayName: this.displayName,
                members: this.members.map((memberKey) => memberKey.toString()),
                description: this.description
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'principals', 'createGroup');
        }

        sendAndParse(): wemQ.Promise<Group> {

            return this.send().then((response: api.rest.JsonResponse<GroupJson>) => {
                return Group.fromJson(response.getResult());
            });
        }

    }
}