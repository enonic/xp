module api {

    export interface Cloneable {

        /**
         * Returns a new instance of this object and ensures that all member variables that are mutable also are cloned
         * (immutable objects can be reused).
         */
        clone() : any;
    }
}