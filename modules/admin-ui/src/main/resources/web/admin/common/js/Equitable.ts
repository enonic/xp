module api {

    /**
     * An interface telling that the object can be tested whether it's equal to another Equitable or not.
     * Inspired by Java's Object.equals method.
     */
    export interface Equitable {

        equals(other: Equitable) : boolean;
    }
}