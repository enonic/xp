module api.task {

    export class GetTaskInfoRequest extends TaskResourceRequest<TaskInfoJson, TaskInfo> {

        protected taskId: TaskId;

        constructor(taskId: TaskId) {
            super();
            this.taskId = taskId;
        }

        getParams(): Object {
            return {};
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), this.taskId.toString());
        }

        sendAndParse(): wemQ.Promise<TaskInfo> {
            return this.send().then((response: api.rest.JsonResponse<TaskInfoJson>) => {
                return TaskInfo.fromJson(response.getResult());
            });
        }
    }

}
