module api.content {

    export class MoveContentResult {

        private moveSuccess: ContentId[];
        private moveFailures: MoveContentResultFailure[];

        constructor(success: ContentId[], failures: MoveContentResultFailure[]) {
            this.moveSuccess = success;
            this.moveFailures = failures;
        }

        getMoved(): ContentId[] {
            return this.moveSuccess;
        }

        getMoveFailures(): MoveContentResultFailure[] {
            return this.moveFailures;
        }

        static fromJson(json: MoveContentResultJson): MoveContentResult {
            var success: ContentId[] = json.successes.map((success) => new ContentId(success.contentId));
            var failure: MoveContentResultFailure[] = json.failures.
                map((failure) => new MoveContentResultFailure(new ContentId(failure.contentId), failure.reason));
            return new MoveContentResult(success, failure);
        }

    }

    export class MoveContentResultFailure {

        private contentId: ContentId;
        private reason: string;

        constructor(contentId: ContentId, reason: string) {
            this.contentId = contentId;
            this.reason = reason;
        }

        getPath(): ContentId {
            return this.contentId;
        }

        getReason(): string {
            return this.reason;
        }
    }
}