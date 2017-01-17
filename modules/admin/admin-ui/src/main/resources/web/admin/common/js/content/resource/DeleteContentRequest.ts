module api.content.resource {

    import TaskState = api.task.TaskState;
    export class DeleteContentRequest extends ContentResourceRequest<api.task.TaskIdJson, api.task.TaskId> {

        private contentPaths: ContentPath[] = [];

        private deleteOnline: boolean;

        constructor(contentPath?: ContentPath) {
            super();
            this.setHeavyOperation(true);
            super.setMethod('POST');
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

        getParams(): Object {
            let fn = (contentPath: ContentPath) => {
                return contentPath.toString();
            };
            return {
                contentPaths: this.contentPaths.map(fn),
                deleteOnline: this.deleteOnline
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'delete');
        }

        sendAndParse(): wemQ.Promise<api.task.TaskId> {
            return this.send().then((response: api.rest.JsonResponse<api.task.TaskIdJson>) => {
                return api.task.TaskId.fromJson(response.getResult());
            });
        }

        sendAndParseWithPolling(): wemQ.Promise<string> {
            return this.send().then((response: api.rest.JsonResponse<api.task.TaskIdJson>) => {
                const deferred = Q.defer<string>();
                const taskId: api.task.TaskId = api.task.TaskId.fromJson(response.getResult());
                const poll = (interval: number = 500) => {
                    setTimeout(() => {
                        new api.task.GetTaskInfoRequest(taskId).sendAndParse().then((task: api.task.TaskInfo) => {
                            let state = task.getState();
                            if (!task) {
                                deferred.reject('Task expired');
                                return; // task probably expired, stop polling
                            }

                            let progress = task.getProgress();

                            switch (state) {
                            case TaskState.FINISHED:
                                deferred.resolve(progress.getInfo());
                                break;
                            case TaskState.FAILED:
                                deferred.reject(progress.getInfo());
                                break;
                            default:
                                poll();
                            }
                        }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                            deferred.reject(reason);
                        }).done();

                    }, interval);
                };
                poll(0);

                return deferred.promise;
            });
        }
    }
}
