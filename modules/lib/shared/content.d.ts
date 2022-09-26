declare global {
    // eslint-disable-next-line @typescript-eslint/no-empty-interface
    interface XpXData {
    }
}

export type WorkflowState = 'IN_PROGRESS' | 'PENDING_APPROVAL' | 'REJECTED' | 'READY';

export type WorkflowCheckState = 'PENDING' | 'REJECTED' | 'APPROVED';

export type ContentInheritType = 'CONTENT' | 'PARENT' | 'NAME' | 'SORT';

export interface Workflow {
    state: WorkflowState;
    checks?: Record<string, WorkflowCheckState>;
}

export interface Attachment {
    name: string;
    label?: string;
    size: number;
    mimeType: string;
}

export interface PublishInfo {
    from?: string;
    to?: string;
    first?: string;
}

export interface Content<Data = Record<string, unknown>, Type extends string = string> {
    _id: string;
    _name: string;
    _path: string;
    _score: number;
    creator: string;
    modifier: string;
    createdTime: string;
    modifiedTime: string;
    owner: string;
    data: Data;
    type: Type;
    displayName: string;
    hasChildren: boolean;
    language: string;
    valid: boolean;
    originProject: string;
    childOrder?: string;
    _sort?: object[];
    page: import('./portal').Component;
    x: XpXData;
    attachments: Record<string, Attachment>;
    publish?: PublishInfo;
    workflow?: Workflow;
    inherit?: ContentInheritType[];
}
