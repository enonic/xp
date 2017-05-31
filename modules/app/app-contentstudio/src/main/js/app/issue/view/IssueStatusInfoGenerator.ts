import {Issue} from '../Issue';
import {IssueType} from '../IssueType';
import DateHelper = api.util.DateHelper;
import User = api.security.User;

export class IssueStatusInfoGenerator {

    private issue: Issue;

    private issueType: IssueType;

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

    public setIssueType(issueType: IssueType): IssueStatusInfoGenerator {
        this.issueType = issueType;
        return this;
    }

    public setCurrentUser(currentUser: User): IssueStatusInfoGenerator {
        this.currentUser = currentUser;
        return this;
    }

    public generate(): string {
        if (this.issueType === IssueType.ASSIGNED_TO_ME) {
            return this.generateAssignedToMe();
        }

        if (this.issueType === IssueType.OPEN) {
            return this.generateOpen();
        }

        if (this.issueType === IssueType.CLOSED) {
            return this.generateClosed();
        }

        return this.generateCreatedByMe();
    }

    private generateAssignedToMe(): string {
        const modifiedDateString: string = DateHelper.getModifiedString(this.issue.getModifiedTime());
        const pattern: string = '#{0} - {1} by {2} {3}'; // id, status, modifier, date

        return api.util.StringHelper.format(pattern, this.issue.getIndex(), this.getStatus(), this.getLastModifiedBy(), modifiedDateString);
    }

    private generateOpen(): string {
        const modifiedDateString: string = DateHelper.getModifiedString(this.issue.getModifiedTime());
        const issueNotModifiedPattern: string = '#{0} - {1} by {2} {3}. Assigned to {4}'; // id, status, modifier, date, assignees
        const issueModifiedPattern: string = '#{0} - Assigned to {1}. {2} by {3} {4}'; // id, assignees, status, modifier, date

        if (this.issue.getModifier()) {
            return api.util.StringHelper.format(issueModifiedPattern, this.issue.getIndex(), this.assignedTo(), this.getStatus(),
                this.getLastModifiedBy(), modifiedDateString);
        }
        else {
            return api.util.StringHelper.format(issueNotModifiedPattern, this.issue.getIndex(), this.getStatus(), this.getLastModifiedBy(),
                modifiedDateString, this.assignedTo());
        }
    }

    private generateClosed(): string {
        const modifiedDateString: string = DateHelper.getModifiedString(this.issue.getModifiedTime());
        const pattern: string = '#{0} - Assigned to {1}. Closed by {2} {3}'; //id, modifier, date, assignees

        return api.util.StringHelper.format(pattern, this.issue.getIndex(), this.assignedTo(), this.getLastModifiedBy(),
            modifiedDateString,
        );
    }

    private generateCreatedByMe(): string {
        const modifiedDateString: string = DateHelper.getModifiedString(this.issue.getModifiedTime());
        const issueNotModifiedPattern: string = '#{0} - {1} {2}. Assigned to {3}'; //id, status, date, assignees
        const issueModifiedPattern: string = '#{0} - Assigned to {1}. {2} {3} '; //id, assignees, status, date

        if (this.issue.getModifier()) {
            return api.util.StringHelper.format(issueModifiedPattern, this.issue.getIndex(), this.assignedTo(), this.getStatus(),
                modifiedDateString);
        }
        else {
            return api.util.StringHelper.format(issueNotModifiedPattern, this.issue.getIndex(), this.getStatus(), modifiedDateString,
                this.assignedTo());
        }
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