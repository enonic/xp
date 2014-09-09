module api.data.json{

    export interface PropertyJson extends DataJson  {

        type:string;

        value?:any;

        set?:DataTypeWrapperJson[];
    }
}