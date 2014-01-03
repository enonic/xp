module api.schema {

    export interface SchemaDeleteJson {

        success: boolean;

        successes?: SuccessJson[];

        failures?: FailureJson[];

    }

    export interface SuccessJson {

        name: string;

    }

    export interface FailureJson {

        name: string;

        reason: string;

    }

}