module api_data_json{

    export interface PropertyJson extends DataJson  {

        type:string;

        value?:string;

        set?:DataTypeWrapperJson[];
    }
}