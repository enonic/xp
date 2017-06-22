import i18n = api.util.i18n;
export enum IssueStatus {
    OPEN, CLOSED
}

export class IssueStatusFormatter {
    public static formatStatus(issueStatus: IssueStatus): string {

        let status;

        switch (issueStatus) {
        case IssueStatus.OPEN:
            status = i18n('field.issue.status.open');
            break;
        case IssueStatus.CLOSED:
            status = i18n('field.issue.status.closed');
            break;
        default:
            status = i18n('field.issue.status.unknown');
        }

        if (IssueStatus[status]) {
            return i18n('field.issue.status.unknown');
        }

        return status;
    }

    public static fromString(value: string): IssueStatus {
        switch (value) {
        case i18n('field.issue.status.open'):
            return IssueStatus.OPEN;
        case i18n('field.issue.status.closed'):
            return IssueStatus.CLOSED;
        default:
            return null;
        }
    }

    public static parseStatus(value: string): IssueStatus {
        switch (value) {
        case 'OPEN':
            return IssueStatus.OPEN;
        case 'CLOSED':
            return IssueStatus.CLOSED;
        default:
            return null;
        }
    }
}
