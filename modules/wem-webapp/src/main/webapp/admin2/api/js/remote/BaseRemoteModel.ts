module api_remote {

    export interface Item {
        editable:bool;
        deletable:bool;
    }

    export interface FailureResult {
        error: string;
    }

}
