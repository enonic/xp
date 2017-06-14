import {Issue} from '../Issue';
import {IssueStatus} from '../IssueStatus';
import DateHelper = api.util.DateHelper;
import User = api.security.User;

export class IssueStatusInfoGenerator {

    private issue: Issue;

    private issueStatus: IssueStatus;

    private currentUser: User;

    private isIdShown: boolean = true;

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

    public setIsIdShown(isIdShown: boolean): IssueStatusInfoGenerator {
        this.isIdShown = isIdShown;
        return this;
    }

    public generate(): string {
        if (this.issueStatus === IssueStatus.CLOSED) {
            return this.generateClosed();
        }

        return this.generateOpen();
    }

    private generateOpen(): string {
        const statusText: string = api.util.StringHelper.format('{0} by {1} {2}', this.getStatus(), this.getLastModifiedBy(),
            this.getModifiedDate());

        if (this.isIdShown) {
            return api.util.StringHelper.format('#{0} - {1}', this.issue.getIndex(), statusText);
        }

        return statusText;
    }

    private generateClosed(): string {
        const pattern: string = 'Closed by {0} {1}'; //id, modifier, date, assignees
        const result: string = api.util.StringHelper.format(pattern, this.getLastModifiedBy(), this.getModifiedDate());

        if (this.isIdShown) {
            return api.util.StringHelper.format('#{0} - {1}', this.issue.getIndex(), result);
        }

        return result;
    }

    private getModifiedDate(): string {
        return DateHelper.getModifiedString(this.issue.getModifiedTime());
    }

    private getStatus(): string {
        if (this.issue.getModifier()) {
            return 'Updated';
        }

        return 'Opened';
    }

    private getLastModifiedBy(): string {
        return '\<span class="creator"\>' + this.getModifiedBy() + '\</span\>';
    }

    private getModifiedBy(): string {
        const lastModifiedBy: string = !!this.issue.getModifier() ? this.issue.getModifier() : this.issue.getCreator();

        if (lastModifiedBy === this.currentUser.getKey().toString()) {
            return 'me';
        }

        return lastModifiedBy;
    }

}
