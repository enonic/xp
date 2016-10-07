module api.content.resource.result {
    
    import DeleteContentResultJson = api.content.json.DeleteContentResultJson;

    export class DeleteContentResult {

        private success: number;
        private pending: number;
        private failureReason: string;

        constructor(builder: DeleteContentResultBuilder) {
            this.success = builder.success;
            this.pending = builder.pending;
            this.failureReason = builder.failureReason;
        }

        getDeleted(): number {
            return this.success;
        }

        getPendings(): number {
            return this.pending;
        }

        getFailureReason(): string {
            return this.failureReason;
        }

        static fromJson(json: DeleteContentResultJson): DeleteContentResult {
            return DeleteContentResult.create().
                setSuccess(json.success).
                setPending(json.pending).
                setFailureReason(json.failureReason).build();
        }

        static create(): DeleteContentResultBuilder {
            return new DeleteContentResultBuilder();
        }

    }

    export class DeleteContentResultBuilder {
        success: number;
        pending: number;
        failureReason: string;

        setSuccess(value: number): DeleteContentResultBuilder {
            this.success = value;
            return this;
        }

        setPending(value: number): DeleteContentResultBuilder {
            this.pending = value;
            return this;
        }

        setFailureReason(value: string): DeleteContentResultBuilder {
            this.failureReason = value;
            return this;
        }

        build(): DeleteContentResult {
            return new DeleteContentResult(this);
        }
    }
}