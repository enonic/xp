module api.content.resource.result {

    import MoveContentResultJson = api.content.json.MoveContentResultJson;

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
            const success: string[] = json.successes.map((value) => value.name);
            const failure: MoveContentResultFailure[] = json.failures.
                map((value) => new MoveContentResultFailure(value.name, value.reason));
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
