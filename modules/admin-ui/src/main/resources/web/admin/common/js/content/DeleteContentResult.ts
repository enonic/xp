module api.content {


    export class DeleteContentResult {

        private deleteSuccess: string[];
        private deletePending: string[];
        private deleteFailures: DeleteContentResultFailure[];

        constructor(success: string[], pending: string[], failures: DeleteContentResultFailure[]) {
            this.deleteSuccess = !!success ? success : [];
            this.deleteFailures = !!failures ? failures : [];
            this.deletePending = !!pending ? pending : [];
        }

        getDeleted(): string[] {
            return this.deleteSuccess;
        }

        getPendings(): string[] {
            return this.deletePending;
        }

        getDeleteFailures(): DeleteContentResultFailure[] {
            return this.deleteFailures;
        }

        static fromJson(json: DeleteContentResultJson): DeleteContentResult {
            if (json.successes) {
                var success: string[] = json.successes.map((success) => success.name);
            }
            if (json.pendings) {
                var pending: string[] = json.pendings.map((pending) => pending.name);
            }
            if (json.failures) {
                var failure: DeleteContentResultFailure[] = json.failures.
                    map((failure) => new DeleteContentResultFailure(failure.name, failure.reason));
            }
            return new DeleteContentResult(success, pending, failure);
        }

    }

    export class DeleteContentResultFailure {

        private name: string;
        private reason: string;

        constructor(name: string, reason: string) {
            this.name = name;
            this.reason = reason;
        }

        getPath(): string {
            return this.name;
        }

        getReason(): string {
            return this.reason;
        }
    }
}