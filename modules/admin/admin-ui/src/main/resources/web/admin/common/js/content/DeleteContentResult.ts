module api.content {


    export class DeleteContentResult {

        private deleteSuccess: DeleteContentResultSuccess[];
        private deletePending: DeleteContentResultPending[];
        private deleteFailures: DeleteContentResultFailure[];

        constructor(success: DeleteContentResultSuccess[], pending: DeleteContentResultPending[], failures: DeleteContentResultFailure[]) {
            this.deleteSuccess = !!success ? success : [];
            this.deleteFailures = !!failures ? failures : [];
            this.deletePending = !!pending ? pending : [];
        }

        getDeleted(): DeleteContentResultSuccess[] {
            return this.deleteSuccess;
        }

        getPendings(): DeleteContentResultPending[] {
            return this.deletePending;
        }

        getDeleteFailures(): DeleteContentResultFailure[] {
            return this.deleteFailures;
        }

        static fromJson(json: DeleteContentResultJson): DeleteContentResult {
            if (json.successes) {
                var success: DeleteContentResultSuccess[] = json.successes.
                    map((success) => new DeleteContentResultSuccess(success.id, success.name));
            }
            if (json.pendings) {
                var pending: DeleteContentResultPending[] = json.pendings.
                    map((pending) => new DeleteContentResultPending(pending.id, pending.name));
            }
            if (json.failures) {
                var failure: DeleteContentResultFailure[] = json.failures.
                    map((failure) => new DeleteContentResultFailure(failure.id, failure.name, failure.reason));
            }
            return new DeleteContentResult(success, pending, failure);
        }

    }

    export class DeleteContentResultSuccess {

        private id: string;
        private name: string;

        constructor(id: string, name: string) {
            this.id = id;
            this.name = name;
        }

        getId(): string {
            return this.id;
        }

        getName(): string {
            return this.name;
        }
    }

    export class DeleteContentResultPending {

        private id: string;
        private name: string;

        constructor(id: string, name: string) {
            this.id = id;
            this.name = name;
        }

        getId(): string {
            return this.id;
        }

        getName(): string {
            return this.name;
        }
    }

    export class DeleteContentResultFailure extends DeleteContentResultSuccess {

        private reason: string;

        constructor(id: string, name: string, reason: string) {
            super(id, name);
            this.reason = reason;
        }

        getReason(): string {
            return this.reason;
        }
    }
}