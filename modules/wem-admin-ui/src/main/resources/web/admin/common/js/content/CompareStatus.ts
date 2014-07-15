module api.content {

    export enum CompareStatus {
        NEW,
        NEWER,
        OLDER,
        CONFLICT,
        DELETED,
        EQUAL
    }
}