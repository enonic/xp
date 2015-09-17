module api.content {

    export class MoveContentResult {

        private moveSuccess: string[];
        private moveFailures: MoveContentResultFailure[];

        constructor(success: string[], failures: MoveContentResultFailure[]) {
            this.moveSuccess = success;
            this.moveFailures = failures;
        }

        getMoved(): string[] {
            return this.moveSuccess;
        }

        getMoveFailures(): MoveContentResultFailure[] {
            return this.moveFailures;
        }

        static fromJson(json: MoveContentResultJson): MoveContentResult {
            var success: string[] = json.successes.map((success) => success.name);
            var failure: MoveContentResultFailure[] = json.failures.
                map((failure) => new MoveContentResultFailure(failure.name, failure.reason));
            return new MoveContentResult(success, failure);
        }

    }

    export class MoveContentResultFailure {

        private name: string;
        private reason: string;

        constructor(name: string, reason: string) {
            this.name = name;
            this.reason = reason;
        }

        getName(): string {
            return this.name;
        }

        getReason(): string {
            return this.reason;
        }
    }
}