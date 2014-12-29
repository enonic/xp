module api.content {

    export class PublishContentRequest extends ContentResourceRequest<PublishContentResult, any> {

        private ids:ContentId[] = [];

        constructor(contentId?:ContentId) {
            super();
            super.setMethod("POST");
            if (contentId) {
                this.addId(contentId);
            }
        }

        setIds(contentIds:ContentId[]):PublishContentRequest {
            this.ids = contentIds;
            return this;
        }

        addId(contentId:ContentId):PublishContentRequest {
            this.ids.push(contentId);
            return this;
        }

        getParams():Object {
            return {
                ids: this.ids.map((el) => { return el.toString(); })
            };
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "publish");
        }

        static feedback(jsonResponse: api.rest.JsonResponse<api.content.PublishContentResult>) {

            var result = jsonResponse.getResult(),
                succeeded = result.successes.length,
                failed = result.failures.length,
                total = succeeded + failed;

            switch (total) {
            case 0:
                api.notify.showFeedback('Nothing to publish.');
                break;
            case 1:
                if (succeeded === 1) {
                    api.notify.showSuccess('Content [' + result.successes[0].name + '] published!');
                } else {
                    api.notify.showError(result.failures[0].reason);
                }
                break;
            default: // > 1
                if (succeeded > 0) {
                    api.notify.showSuccess('[' + succeeded + '] contents published!');
                }
                if (failed > 0) {
                    api.notify.showError('[' + failed + '] contents failed to publish!');
                }
            }
        }
    }
}
