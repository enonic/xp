module api.system {

    export interface StatusJson {
        installation:string;
        version:string;
        context?: {
            authenticated: boolean;
            principals: string[];
        }
    }

}
