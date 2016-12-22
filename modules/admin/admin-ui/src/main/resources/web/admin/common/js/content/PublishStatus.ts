module api.content {

    export enum PublishStatus {
        ONLINE, PENDING, EXPIRED
    }

    export class PublishStatusFormatter {
        public static formatStatus(publishStatus: PublishStatus): string {

            var status;

            switch (publishStatus) {
            case PublishStatus.ONLINE:
                status = "Online";
                break;
            case PublishStatus.PENDING:
                status = "Pending";
                break;
            case PublishStatus.EXPIRED:
                status = "Expired";
                break;
            default:
                status = "Unknown";
            }

            if (!!CompareStatus[status]) {
                return "Unknown";
            }

            return status;
        }
    }
}