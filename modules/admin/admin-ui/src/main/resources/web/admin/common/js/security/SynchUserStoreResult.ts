module api.security {

    export class SynchUserStoreResult {

        private userStoreKey: UserStoreKey;
        private synchronized: boolean;
        private reason: string;

        constructor() {
        }

        getUserStoreKey(): UserStoreKey {
            return this.userStoreKey;
        }

        isSynchronized(): boolean {
            return this.synchronized;
        }

        getReason(): string {
            return this.reason;
        }

        static fromJson(json: api.security.SynchUserStoreResultJson): SynchUserStoreResult {
            var result = new SynchUserStoreResult();
            result.userStoreKey = UserStoreKey.fromString(json.userStoreKey);
            result.synchronized = json.synchronized;
            result.reason = json.reason;
            return result;
        }
    }

}