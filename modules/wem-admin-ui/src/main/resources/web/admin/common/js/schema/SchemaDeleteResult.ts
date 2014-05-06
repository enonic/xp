module api.schema {

    export class SchemaDeleteResult {

        private success: boolean;
        private successes: SuccessResult[];
        private failures: FailureResult[];

        constructor(json: SchemaDeleteJson) {
            this.success = json.success;
            this.successes = SuccessResult.fromJsonArray(json.successes);
            this.failures = FailureResult.fromJsonArray(json.failures);
        }

        isSuccess():boolean {
            return this.success;
        }

        getSuccesses():SuccessResult[] {
            return this.successes;
        }

        getFailures():FailureResult[] {
            return this.failures;
        }

    }

    export class SuccessResult {

        private name:string;

        static fromJsonArray(jsonArray:SuccessJson[]):SuccessResult[] {
            var array:SuccessResult[] = [];
            jsonArray.forEach((json:SuccessJson) => {
                array.push(new SuccessResult(json));
            });
            return array;
        }

        constructor(json:SuccessJson) {
            this.name = json.name;
        }

        getName():string {
            return this.name;
        }
    }

    export class FailureResult {

        private name:string;
        private reason:string;

        static fromJsonArray(jsonArray:FailureJson[]):FailureResult[] {
            var array:FailureResult[] = [];
            jsonArray.forEach((json:FailureJson) => {
                array.push(new FailureResult(json));
            });
            return array;
        }

        constructor(json:FailureJson) {
            this.name = json.name;
            this.reason = json.reason;
        }

        getName():string {
            return this.name;
        }

        getReason():string {
            return this.reason;
        }

    }

}