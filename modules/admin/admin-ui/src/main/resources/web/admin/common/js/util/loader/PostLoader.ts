module api.util.loader {

    export class PostLoader<JSON, OBJECT> extends BaseLoader<JSON, OBJECT> {

        private isPostLoading: boolean = false;

        constructor(request: api.rest.ResourceRequest<JSON, OBJECT[]>) {
            super(request);
        }

        sendRequest(): wemQ.Promise<OBJECT[]> {
            if (!this.isPostLoading) {
                this.resetParams();
            }
            return super.sendRequest();
        }

        resetParams() {
            // 
        }

        isPartiallyLoaded(): boolean {
            return true;
        }

        postLoad() {
            // already have elements and not more than total
            if (this.isPartiallyLoaded() && this.isLoaded()) {
                this.isPostLoading = true;
                this.load(true).then(() => {
                    this.isPostLoading = false;
                }).catch(() => {
                    this.isPostLoading = false;
                });
            }
        }
    }
}