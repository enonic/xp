module api.security {

    export class SyncUserStoreResult {

        private userStoreKey: UserStoreKey;
        private synchronized: boolean;
        private reason: string;

        getUserStoreKey(): UserStoreKey {
            return this.userStoreKey;
        }

        isSynchronized(): boolean {
            return this.synchronized;
        }

        getReason(): string {
            return this.reason;
        }

        static fromJson(json: api.security.SyncUserStoreResultJson): SyncUserStoreResult {
            let result = new SyncUserStoreResult();
            result.userStoreKey = UserStoreKey.fromString(json.userStoreKey);
            result.synchronized = json.synchronized;
            result.reason = json.reason;
            return result;
        }
    }

}
