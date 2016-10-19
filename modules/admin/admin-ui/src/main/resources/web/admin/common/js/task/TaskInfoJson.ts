module api.task {

    export interface TaskInfoJson {
        id: string;
        description: string;
        state: string;
        progress: TaskProgressJson;
    }
}
