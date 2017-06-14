import {Issue} from './Issue';
import {IssueWithAssigneesJson} from './json/IssueWithAssigneesJson';
import User = api.security.User;

export class IssueWithAssignees {

    private issue: Issue;

    private assignees: User[];

    constructor(issue: Issue, assignees?: User[]) {
        this.issue = issue;
        this.assignees = assignees;
    }

    getIssue(): Issue {
        return this.issue;
    }

    getAssignees(): User[] {
        return this.assignees;
    }

    static fromJson(json: IssueWithAssigneesJson): IssueWithAssignees {
        const issue: Issue = Issue.fromJson(json.issue);
        const assignees: User[] = json.assignees ? json.assignees.map(assignee => User.fromJson(assignee)) : null;

        return new IssueWithAssignees(issue, assignees);
    }
}
