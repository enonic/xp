module api.content {

    export class PublishContentRequest extends ContentResourceRequest<PublishContentResult, any> {

        private ids: ContentId[] = [];

        constructor(contentId?: ContentId) {
            super();
            super.setMethod("POST");
            if (contentId) {
                this.addId(contentId);
            }
        }

        setIds(contentIds: ContentId[]): PublishContentRequest {
            this.ids = contentIds;
            return this;
        }

        addId(contentId: ContentId): PublishContentRequest {
            this.ids.push(contentId);
            return this;
        }

        getParams(): Object {
            return {
                ids: this.ids.map((el) => {
                    return el.toString();
                })
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "publish");
        }

        static feedback(jsonResponse: api.rest.JsonResponse<api.content.PublishContentResult>) {

            var result = jsonResponse.getResult(),
                succeeded = result.successes.length,
                failed = result.failures.length,
                deleted = result.deleted.length,
                total = succeeded + failed + deleted;

            switch (total) {
            case 0:
                api.notify.showFeedback('Nothing to publish.');
                break;
            case 1:
                if (succeeded === 1) {
                    api.notify.showSuccess('\"' + result.successes[0].name + '\" published');
                } else if (failed === 1) {
                    api.notify.showError('\"' + result.failures[0].name + '\" failed, reason: ' + result.failures[0].reason);
                } else {
                    api.notify.showSuccess('\"' + result.deleted[0].name + '\" deleted');
                }
                break;
            default: // > 1
                if (succeeded > 0) {
                    api.notify.showSuccess('\"' + succeeded + '\" items published');
                }
                if (deleted > 0) {
                    api.notify.showSuccess('\"' + deleted + '\" items marked for deletion');
                }
                if (failed > 0) {
                    api.notify.showError('\"' + failed + '\" items failed to publish');
                }
            }
        }
    }
}
