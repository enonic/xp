module api.content {


    /*
     These statuses should be translated:
     NEW -> New
     NEWER -> Modified
     OLDER, CONFLICT -> Conflict
     DELETED -> Deleted
     EQUAL -> Online
     */
    export enum CompareStatus {
        NEW,
        NEWER,
        OLDER,
        CONFLICT,
        DELETED,
        EQUAL
    }
}