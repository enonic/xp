module api.issue.json {

    export interface IssueSummaryJson {

        id: any;

        index: number;

        title: string;

        creator: any;

        modifier: any;

        modifiedTime: string;

        description: string;

        issueStatus: string;
    }
}
