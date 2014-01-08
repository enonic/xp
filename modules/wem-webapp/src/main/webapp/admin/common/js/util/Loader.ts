module api.util {

    export interface Loader extends api.event.Observable {

        search(searchString:string);
    }
}