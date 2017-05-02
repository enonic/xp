import '../../api.ts';
import {IssueSummary, IssueSummaryBuilder} from './IssueSummary';
import {IssueJson} from './IssueJson';
import PublishRequest = api.issue.PublishRequest;
import PrincipalKey = api.security.PrincipalKey;

export class Issue extends IssueSummary {

    private approvers: PrincipalKey[];

    private publishRequest: PublishRequest;

    constructor(builder: IssueBuilder) {
        super(builder);

        this.approvers = builder.approvers;
        this.publishRequest = builder.publishRequest;
    }

    public getApprovers(): PrincipalKey[] {
        return this.approvers;
    }

    public getPublishRequest(): PublishRequest {
        return this.publishRequest;
    }

    static fromJson(json: IssueJson): Issue {
        return new IssueBuilder().fromJson(json).build();
    }

    static create(): IssueBuilder {
        return new IssueBuilder();
    }
}

export class IssueBuilder extends IssueSummaryBuilder {

    approvers: PrincipalKey[] = [];

    publishRequest: PublishRequest;

    fromJson(json: IssueJson): IssueBuilder {
        super.fromJson(json);
        this.approvers = json.approverIds ? json.approverIds.map(approver => PrincipalKey.fromString(approver)) : [];
        this.publishRequest = json.publishRequest ? PublishRequest.create().fromJson(json.publishRequest).build() : null;

        return this;
    }

    setApprovers(value: PrincipalKey[]): IssueBuilder {
        this.approvers = value;
        return this;
    }

    setPublishRequest(value: PublishRequest): IssueBuilder {
        this.publishRequest = value;
        return this;
    }

    setDescription(value: string): IssueBuilder {
        this.description = value;
        return this;
    }

    build(): Issue {
        return new Issue(this);
    }
}
