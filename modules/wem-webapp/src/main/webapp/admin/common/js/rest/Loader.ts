module api_rest {

    export interface Loader extends api_event.Observable {

        search(searchString:string);
    }
}