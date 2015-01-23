module api.content {

    export enum CompareStatus {
        NEW,
        NEW_TARGET,
        NEWER,
        OLDER,
        DELETED,
        DELETED_TARGET,
        EQUAL,
        MOVED,
        CONFLICT_PATH_EXISTS,
        CONFLICT_VERSION_BRANCH_DIVERGS,
        UNKNOWN
    }
}