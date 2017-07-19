module api.content {

    import i18n = api.util.i18n;

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
                    status = i18n('status.new');
                } else {
                    status = i18n('status.offline');
                }
                break;
            case CompareStatus.NEWER:
                status = i18n('status.modified');
                break;
            case CompareStatus.OLDER:
                status = i18n('status.outofdate');
                break;
            case CompareStatus.PENDING_DELETE:
                status = i18n('status.deleted');
                break;
            case CompareStatus.EQUAL:
                status = i18n('status.online');
                break;
            case CompareStatus.MOVED:
                status = i18n('status.moved');
                break;
            case CompareStatus.PENDING_DELETE_TARGET:
                status = i18n('status.deletedinprod');
                break;
            case CompareStatus.NEW_TARGET:
                status = i18n('status.newinprod');
                break;
            case CompareStatus.CONFLICT_PATH_EXISTS:
                status = i18n('status.conflict');
                break;
            default:
                status = i18n('status.unknown');
            }

            if (!!CompareStatus[status]) {
                return i18n('status.unknown');
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

        public static isNew(compareStatus: CompareStatus): boolean {
            return compareStatus === CompareStatus.NEW;
        }
    }

}
