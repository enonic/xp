module api_remote {

    export interface Item {
        editable:boolean;
        deletable:boolean;
    }

    export interface FailureResult {
        error: string;
    }

}
