module api.data2 {

    export interface PropertyIdProvider {

        getNextId(): PropertyId;
    }
}