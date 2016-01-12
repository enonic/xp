module api.security {

    export interface UserStoreAuthConfigJson {
        applicationKey: string;
        config: api.data.PropertyArrayJson[];
    }
}