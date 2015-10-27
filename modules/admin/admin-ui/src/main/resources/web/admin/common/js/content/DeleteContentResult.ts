module api.content {


    export class DeleteContentResult {

        private deleteSuccess: DeleteContentResultSuccess[];
        private deletePending: string[];
        private deleteFailures: DeleteContentResultFailure[];

        constructor(success: DeleteContentResultSuccess[], pending: string[], failures: DeleteContentResultFailure[]) {
            this.deleteSuccess = !!success ? success : [];
            this.deleteFailures = !!failures ? failures : [];
            this.deletePending = !!pending ? pending : [];
        }

        getDeleted(): DeleteContentResultSuccess[] {
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
                var success: DeleteContentResultSuccess[] = json.successes.
                    map((success) => new DeleteContentResultSuccess(success.id, success.name, success.type));
            }
            if (json.pendings) {
                var pending: string[] = json.pendings.map((pending) => pending.name);
            }
            if (json.failures) {
                var failure: DeleteContentResultFailure[] = json.failures.
                    map((failure) => new DeleteContentResultFailure(failure.id, failure.name, failure.type, failure.reason));
            }
            return new DeleteContentResult(success, pending, failure);
        }

    }

    export class DeleteContentResultSuccess {

        private id: string;
        private name: string;
        private type: string;

        constructor(id: string, name: string, type: string) {
            this.id = id;
            this.name = name;
            this.type = type;
        }

        getId(): string {
            return this.id;
        }

        getName(): string {
            return this.name;
        }

        getType(): string {
            return this.type;
        }
    }

    export class DeleteContentResultFailure extends DeleteContentResultSuccess {

        private reason: string;

        constructor(id: string, name: string, type: string, reason: string) {
            super(id, name, type);
            this.reason = reason;
        }

        getReason(): string {
            return this.reason;
        }
    }
}