module api.security {

    export interface AuthConfigJson {
        applicationKey: string;
        config: api.data.PropertyArrayJson[];
    }
}
