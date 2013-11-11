module api_schema {

    export interface SchemaDeleteJson {

        successes?: SuccessJson[];

        failures?: FailureJson[];

    }

    export interface SuccessJson {

        name: string;

    }

    export interface FailureJson {

        name: string;

    }

}