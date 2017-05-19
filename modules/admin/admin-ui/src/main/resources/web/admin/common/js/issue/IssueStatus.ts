module api.issue {

    export enum IssueStatus {
        OPEN, CLOSED
    }

    export class IssueStatusFormatter {
        public static formatStatus(issueStatus: IssueStatus): string {

            let status;

            switch (issueStatus) {
            case IssueStatus.OPEN:
                status = 'Open';
                break;
            case IssueStatus.CLOSED:
                status = 'Closed';
                break;
            default:
                status = 'Unknown';
            }

            if (IssueStatus[status]) {
                return 'Unknown';
            }

            return status;
        }

        public static fromString(value: string): IssueStatus {
            switch (value) {
            case 'Open':
                return IssueStatus.OPEN;
            case 'Closed':
                return IssueStatus.CLOSED;
            default:
                return null;
            }
        }
    }

}
