module api.content.resource.result {
    
    import DeleteContentResultJson = api.content.json.DeleteContentResultJson;

    export class DeleteContentResult {

        private success: number;
        private pending: number;
        private contentName: string;
        private contentType: string;
        private failureReason: string;

        constructor(builder: DeleteContentResultBuilder) {
            this.success = builder.success;
            this.pending = builder.pending;
            this.contentName = builder.contentName;
            this.contentType = builder.contentType;
            this.failureReason = builder.failureReason;
        }

        getDeleted(): number {
            return this.success;
        }

        getPendings(): number {
            return this.pending;
        }

        getContentName(): string {
            return this.contentName;
        }

        getContentType(): string {
            return this.contentType;
        }

        getFailureReason(): string {
            return this.failureReason;
        }

        static fromJson(json: DeleteContentResultJson): DeleteContentResult {
            return DeleteContentResult.create().
                setSuccess(json.success).
                setPending(json.pending).
                setContentName(json.contentName).
                setContentType(json.contentType).
                setFailureReason(json.failureReason).build();
        }

        static create(): DeleteContentResultBuilder {
            return new DeleteContentResultBuilder();
        }

    }

    export class DeleteContentResultBuilder {
        success: number;
        pending: number;
        contentName: string;
        contentType: string;
        failureReason: string;

        setSuccess(value: number): DeleteContentResultBuilder {
            this.success = value;
            return this;
        }

        setPending(value: number): DeleteContentResultBuilder {
            this.pending = value;
            return this;
        }

        setContentName(value: string): DeleteContentResultBuilder {
            this.contentName = value;
            return this;
        }

        setContentType(value: string): DeleteContentResultBuilder {
            this.contentType = value;
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