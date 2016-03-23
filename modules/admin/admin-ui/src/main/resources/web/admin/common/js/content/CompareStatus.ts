module api.content {

    export enum CompareStatus {
        NEW,
        NEW_TARGET,
        NEWER,
        OLDER,
        PENDING_DELETE,
        PENDING_DELETE_TARGET,
        EQUAL,
        MOVED,
        CONFLICT_PATH_EXISTS,
        CONFLICT_VERSION_BRANCH_DIVERGS,
        UNKNOWN
    }

    export class CompareStatusFormatter {
        public static formatStatus(compareStatus: CompareStatus): string {

            var status;

            switch (compareStatus) {
            case CompareStatus.NEW:
                status = "Offline";
                break;
            case CompareStatus.NEWER:
                status = "Modified";
                break;
            case CompareStatus.OLDER:
                status = "Out-of-date";
                break;
            case CompareStatus.PENDING_DELETE:
                status = "Pending delete";
                break;
            case CompareStatus.EQUAL:
                status = "Online";
                break;
            case CompareStatus.MOVED:
                status = "Moved";
                break;
            case CompareStatus.PENDING_DELETE_TARGET:
                status = "Deleted in prod";
                break;
            case CompareStatus.NEW_TARGET:
                status = "New in prod";
                break;
            case CompareStatus.CONFLICT_PATH_EXISTS:
                status = "Conflict";
                break;
            default:
                status = "Unknown"
            }

            if (!!CompareStatus[status]) {
                return "Unknown";
            }

            return status;
        }
    }
}