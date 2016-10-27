module api.task {

    export interface TaskProgressJson {
        info: string;
        current: number;
        total: number;
    }
}
