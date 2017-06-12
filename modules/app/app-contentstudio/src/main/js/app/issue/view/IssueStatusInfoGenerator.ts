import {Issue} from '../Issue';
import {IssueStatus} from '../IssueStatus';
import DateHelper = api.util.DateHelper;
import User = api.security.User;

export class IssueStatusInfoGenerator {

    private issue: Issue;

    private issueStatus: IssueStatus;

    private currentUser: User;

    private constructor() {
    }

    public static create(): IssueStatusInfoGenerator {
        return new IssueStatusInfoGenerator();
    }

    public setIssue(issue: Issue): IssueStatusInfoGenerator {
        this.issue = issue;
        return this;
    }

    public setIssueStatus(issueStatus: IssueStatus): IssueStatusInfoGenerator {
        this.issueStatus = issueStatus;
        return this;
    }

    public setCurrentUser(currentUser: User): IssueStatusInfoGenerator {
        this.currentUser = currentUser;
        return this;
    }

    public generate(): string {
        if (this.issueStatus === IssueStatus.CLOSED) {
            return this.generateClosed();
        }

        return this.generateOpen();
    }

    private generateOpen(): string {
        const assignedToText: string = 'Assigned to ' + this.assignedTo();
        const statusText: string = api.util.StringHelper.format('{0} by {1} {2}', this.getStatus(), this.getLastModifiedBy(),
            this.getModifiedDate());

        const result: string = !!this.issue.getModifier() ? (assignedToText + '. ' + statusText) : (statusText + '. ' + assignedToText);

        return result;
    }

    private generateClosed(): string {
        const pattern: string = 'Assigned to {0}. Closed by {1} {2}'; //id, modifier, date, assignees
        const result: string = api.util.StringHelper.format(pattern, this.assignedTo(), this.getLastModifiedBy(), this.getModifiedDate());

        return result;
    }

    private getModifiedDate(): string {
        return DateHelper.getModifiedString(this.issue.getModifiedTime());
    }

    private getStatus(): string {
        if (this.issue.getModifier()) {
            return 'Updated'
        }

        return 'Opened';
    }

    private getLastModifiedBy(): string {
        return '\<span class="creator"\>' + this.getModifiedBy() + '\</span\>'
    }

    private getModifiedBy(): string {
        const lastModifiedBy: string = !!this.issue.getModifier() ? this.issue.getModifier() : this.issue.getCreator();

        if (lastModifiedBy === this.currentUser.getKey().toString()) {
            return 'me';
        }

        return lastModifiedBy;
    }

    private assignedTo(): string {
        return '\<span class="assignee"\>' + this.getAssignedTo() + '\</span\>' + (this.issue.getApprovers().length > 1
                ? ' users'
                : '');
    }

    private getAssignedTo(): string {
        if (this.issue.getApprovers().length > 1) {
            return this.issue.getApprovers().length.toString();
        }

        const assignee: string = this.issue.getApprovers()[0].toString();

        if (assignee === this.currentUser.getKey().toString()) {
            return 'me';
        }

        return this.issue.getApprovers()[0].toString();
    }

}