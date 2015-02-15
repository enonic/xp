module api.content {


    export class DeleteContentResult {

        private deleteSuccess: ContentPath[];
        private deleteFailures: DeleteContentResultFailure[];

        constructor(success: ContentPath[], failures: DeleteContentResultFailure[]) {
            this.deleteSuccess = success;
            this.deleteFailures = failures;
        }

        getDeleted(): ContentPath[] {
            return this.deleteSuccess;
        }

        getDeleteFailures(): DeleteContentResultFailure[] {
            return this.deleteFailures;
        }

        static fromJson(json: DeleteContentResultJson): DeleteContentResult {
            var success: ContentPath[] = json.successes.map((success) => ContentPath.fromString(success.path));
            var failure: DeleteContentResultFailure[] = json.failures.
                map((success) => new DeleteContentResultFailure(ContentPath.fromString(success.path), success.reason));
            return new DeleteContentResult(success, failure);
        }

    }

    export class DeleteContentResultFailure {

        private path: ContentPath;
        private reason: string;

        constructor(path: ContentPath, reason: string) {
            this.path = path;
            this.reason = reason;
        }

        getPath(): ContentPath {
            return this.path;
        }

        getReason(): string {
            return this.reason;
        }
    }
}