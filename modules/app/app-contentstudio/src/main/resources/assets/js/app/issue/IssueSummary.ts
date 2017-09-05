import {IssueStatus, IssueStatusFormatter} from './IssueStatus';
import {IssueSummaryJson} from './json/IssueSummaryJson';
export class IssueSummary {

    private id: string;

    private index: number;

    private title: string;

    private creator: string;

    private modifier: string;

    private description: string;

    private issueStatus: IssueStatus;

    private modifiedTime: Date;

    constructor(builder: IssueSummaryBuilder) {
        this.id = builder.id;
        this.index = builder.index;
        this.title = builder.title;
        this.creator = builder.creator;
        this.modifier = builder.modifier;
        this.modifiedTime = builder.modifiedTime;
        this.description = builder.description;
        this.issueStatus = builder.issueStatus;
    }

    static fromJson(json: IssueSummaryJson): IssueSummary {
        return new IssueSummaryBuilder().fromJson(json).build();
    }

    static create(): IssueSummaryBuilder {
        return new IssueSummaryBuilder();
    }

    getId(): string {
        return this.id;
    }

    getIndex(): number {
        return this.index;
    }

    getTitle(): string {
        return this.title;
    }

    getTitleWithId(): string {
        return `${this.title} (#${this.index})`;
    }

    getCreator(): string {
        return this.creator;
    }

    getModifier(): string {
        return this.modifier;
    }

    getModifiedTime(): Date {
        return this.modifiedTime;
    }

    getDescription(): string {
        return this.description.trim();
    }

    getIssueStatus(): IssueStatus {
        return this.issueStatus;
    }

}

export class IssueSummaryBuilder {

    id: string;

    index: number;

    title: string;

    creator: string;

    modifier: string;

    modifiedTime: Date;

    description: string;

    issueStatus: IssueStatus;

    fromJson(json: IssueSummaryJson): IssueSummaryBuilder {
        this.id = json.id;
        this.index = json.index;
        this.title = json.title;
        this.creator = json.creator;
        this.modifier = json.modifier;
        this.modifiedTime = json.modifiedTime ? new Date(Date.parse(json.modifiedTime)) : null;
        this.description = json.description;
        this.issueStatus = IssueStatusFormatter.parseStatus(json.issueStatus);

        return this;
    }

    setId(id: string): IssueSummaryBuilder {
        this.id = id;
        return this;
    }

    setIndex(index: number): IssueSummaryBuilder {
        this.index = index;
        return this;
    }

    setTitle(title: string): IssueSummaryBuilder {
        this.title = title;
        return this;
    }

    setCreator(creator: string): IssueSummaryBuilder {
        this.creator = creator;
        return this;
    }

    setModifier(modifier: string): IssueSummaryBuilder {
        this.modifier = modifier;
        return this;
    }

    setModifiedTime(modifiedTime: Date): IssueSummaryBuilder {
        this.modifiedTime = modifiedTime;
        return this;
    }

    setDescription(description: string): IssueSummaryBuilder {
        this.description = description;
        return this;
    }

    setIssueStatus(issueStatus: IssueStatus): IssueSummaryBuilder {
        this.issueStatus = issueStatus;
        return this;
    }

    build(): IssueSummary {
        return new IssueSummary(this);
    }
}
