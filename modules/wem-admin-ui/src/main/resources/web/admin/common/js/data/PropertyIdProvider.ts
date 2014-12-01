module api.data {

    export interface PropertyIdProvider {

        getNextId(): PropertyId;
    }
}