import {Issue} from '../Issue';
import {IssueStatus} from '../IssueStatus';
import DateHelper = api.util.DateHelper;
import User = api.security.User;
import i18n = api.util.i18n;

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
        let textKey;
        if (this.issueStatus === IssueStatus.CLOSED) {
            textKey = 'field.issue.closed';
        } else if (this.issue.getModifier()) {
            textKey = 'field.issue.updated';
        } else {
            textKey = 'field.issue.opened';
        }

        return i18n(textKey, this.getLastModifiedBy(), this.getModifiedDate());
    }

    private getModifiedDate(): string {
        return DateHelper.getModifiedString(this.issue.getModifiedTime());
    }

    private getLastModifiedBy(): string {
        return '\<span class="creator"\>' + this.getModifiedBy() + '\</span\>';
    }

    private getModifiedBy(): string {
        const lastModifiedBy: string = !!this.issue.getModifier() ? this.issue.getModifier() : this.issue.getCreator();

        if (lastModifiedBy === this.currentUser.getKey().toString()) {
            return i18n('field.me');
        }

        return lastModifiedBy;
    }

}
