module api.issue.json {
    export interface IssueStatsJson {

        assignedToMe: number;

        createdByMe: number;

        open: number;

        closed: number;
    }
}
