module api_remote {

    export interface BaseResult {
        success: bool;
        error?: string;
    }

    export interface FailureResult {
        error: string;
    }

}
