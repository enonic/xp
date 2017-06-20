import {IssueJson} from './IssueJson';
import UserJson = api.security.UserJson;

export interface IssueWithAssigneesJson {

    issue: IssueJson;

    assignees: UserJson[];
}
