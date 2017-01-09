module api.security {

    export class DeleteUserStoreResult {

        private userStoreKey: UserStoreKey;
        private deleted: boolean;
        private reason: string;

        getUserStoreKey(): UserStoreKey {
            return this.userStoreKey;
        }

        isDeleted(): boolean {
            return this.deleted;
        }

        getReason(): string {
            return this.reason;
        }

        static fromJson(json: api.security.DeleteUserStoreResultJson): DeleteUserStoreResult {
            let result = new DeleteUserStoreResult();
            result.userStoreKey = UserStoreKey.fromString(json.userStoreKey);
            result.deleted = json.deleted;
            result.reason = json.reason;
            return result;
        }
    }

}