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

        public static formatStatusFromContent(content: ContentSummaryAndCompareStatus): string {
            if(content) {
                return CompareStatusFormatter.formatStatus(content.getCompareStatus(), content.getContentSummary());
            }
        }

        public static formatStatus(compareStatus: CompareStatus, content?:ContentSummary): string {

            let status;

            switch (compareStatus) {
            case CompareStatus.NEW:
                if(content && !content.getPublishFirstTime()) {
                    status = 'New';
                } else {
                    status = 'Offline';
                }
                break;
            case CompareStatus.NEWER:
                status = 'Modified';
                break;
            case CompareStatus.OLDER:
                status = 'Out-of-date';
                break;
            case CompareStatus.PENDING_DELETE:
                status = 'Deleted';
                break;
            case CompareStatus.EQUAL:
                status = 'Online';
                break;
            case CompareStatus.MOVED:
                status = 'Moved';
                break;
            case CompareStatus.PENDING_DELETE_TARGET:
                status = 'Deleted in prod';
                break;
            case CompareStatus.NEW_TARGET:
                status = 'New in prod';
                break;
            case CompareStatus.CONFLICT_PATH_EXISTS:
                status = 'Conflict';
                break;
            default:
                status = 'Unknown';
            }

            if (!!CompareStatus[status]) {
                return 'Unknown';
            }

            return status;
        }
    }

    export class CompareStatusChecker {

        public static isPendingDelete(compareStatus: CompareStatus): boolean {
            return compareStatus == CompareStatus.PENDING_DELETE;
        }

        public static isPublished(compareStatus: CompareStatus): boolean {
            return compareStatus !== CompareStatus.NEW && compareStatus !== CompareStatus.UNKNOWN;
        }

        public static isOnline(compareStatus: CompareStatus): boolean {
            return compareStatus === CompareStatus.EQUAL;
        }
    }

}
