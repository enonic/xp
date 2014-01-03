module api.rest {

    export interface Loader extends api.event.Observable {

        search(searchString:string);
    }
}