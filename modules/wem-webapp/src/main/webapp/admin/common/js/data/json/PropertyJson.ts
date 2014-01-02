module api.data.json{

    export interface PropertyJson extends DataJson  {

        type:string;

        value?:string;

        set?:DataTypeWrapperJson[];
    }
}