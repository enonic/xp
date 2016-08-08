module api.task {


    export class TaskInfo {

        private id: TaskId;
        private description: string;
        private state: TaskState;
        private progress: TaskProgress;

        constructor(builder: TaskInfoBuilder) {
            this.id = builder.id;
            this.description = builder.description;
            this.state = builder.state;
            this.progress = builder.progress;
        }

        getId(): TaskId {
            return this.id;
        }

        getDescription(): string {
            return this.description;
        }

        getState(): TaskState {
            return this.state;
        }

        getProgress(): TaskProgress {
            return this.progress;
        }

        static fromJson(json: api.task.TaskInfoJson) {
            if (!json.id) {
                return null;
            }
            return TaskInfo.create().
                setId(TaskId.fromString(json.id)).
                setDescription(json.description).
                setState(TaskState[json.state]).
                setProgress(TaskProgress.create().fromJson(json.progress).build()).
                build();
        }

        static create(): TaskInfoBuilder {
            return new TaskInfoBuilder();
        }
    }

    export class TaskInfoBuilder {

        id: TaskId;
        description: string;
        state: TaskState;
        progress: TaskProgress;

        fromSource(source: TaskInfo): TaskInfoBuilder {
            this.id = source.getId();
            this.description = source.getDescription();
            this.state = source.getState();
            this.progress = source.getProgress();
            return this;
        }

        setId(id: TaskId): TaskInfoBuilder {
            this.id = id;
            return this;
        }

        setDescription(description: string): TaskInfoBuilder {
            this.description = description;
            return this;
        }

        setState(state: TaskState): TaskInfoBuilder {
            this.state = state;
            return this;
        }

        setProgress(progress: TaskProgress): TaskInfoBuilder {
            this.progress = progress;
            return this;
        }

        build(): TaskInfo {
            return new TaskInfo(this);
        }
    }
}