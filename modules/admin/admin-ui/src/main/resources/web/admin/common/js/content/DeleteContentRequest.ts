module api.content {

    export class DeleteContentRequest extends ContentResourceRequest<DeleteContentResultJson, DeleteContentResult> {

        private contentPaths: ContentPath[] = [];

        private deleteOnline: boolean;

        private deletePending: boolean;

        constructor(contentPath?: ContentPath) {
            super();
            this.setHeavyOperation(true);
            super.setMethod("POST");
            if (contentPath) {
                this.addContentPath(contentPath);
            }
        }

        setContentPaths(contentPaths: ContentPath[]): DeleteContentRequest {
            this.contentPaths = contentPaths;
            return this;
        }

        addContentPath(contentPath: ContentPath): DeleteContentRequest {
            this.contentPaths.push(contentPath);
            return this;
        }

        setDeleteOnline(deleteOnline: boolean) {
            this.deleteOnline = deleteOnline;
        }

        setDeletePending(deletePending: boolean) {
            this.deletePending = deletePending;
        }

        getParams(): Object {
            var fn = (contentPath: ContentPath) => {
                return contentPath.toString();
            };
            return {
                contentPaths: this.contentPaths.map(fn),
                deleteOnline: this.deleteOnline,
                deletePending: this.deletePending
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "delete");
        }

        sendAndParse(): wemQ.Promise<DeleteContentResult> {

            return this.send().
                then((response: api.rest.JsonResponse<DeleteContentResultJson>) => {

                    return DeleteContentResult.fromJson(response.getResult());

                });
        }

        static feedback(result: api.content.DeleteContentResult) {
            var successes = result.getDeleted().length,
                pendings = result.getPendings().length,
                failures = result.getDeleteFailures().length,
                total = successes + failures + pendings;

            switch (total) {
            case 0:
                api.notify.showFeedback('Nothing to delete.');
                break;
            case 1:
                if (successes === 1) {
                    api.notify.showSuccess('\"' + result.getDeleted()[0].getName() + '\" deleted');
                } else if (pendings === 1) {
                    api.notify.showSuccess('\"' + result.getPendings()[0].getName() + '\" marked for deletion');
                } else if (failures === 1) {
                    api.notify.showError('\"' + result.getDeleteFailures()[0].getName() + '\" deletion failed, reason: '
                                         + result.getDeleteFailures()[0].getReason());
                }
                break;
            default: // > 1
                if (successes > 0) {
                    api.notify.showSuccess(successes + ' items were deleted');
                }
                if (pendings > 0) {
                    api.notify.showSuccess(pendings + ' items were marked for deletion');
                }
                if (failures > 0) {
                    api.notify.showError(failures + ' items failed to delete');
                }

                break
            }
        }
    }
}