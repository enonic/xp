module api.security {

    export class DeletePathGuardResult {

        private key: string;
        private deleted: boolean;
        private reason: string;

        constructor() {
        }

        getKey(): string {
            return this.key;
        }

        isDeleted(): boolean {
            return this.deleted;
        }

        getReason(): string {
            return this.reason;
        }

        static fromJson(json: api.security.DeletePathGuardResultJson): DeletePathGuardResult {
            var result = new DeletePathGuardResult();
            result.key = json.key;
            result.deleted = json.deleted;
            result.reason = json.reason;
            return result;
        }
    }

}