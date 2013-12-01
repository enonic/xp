module api_remote_contenttype {

    export interface ContentType extends api_remote.Item {
        name: string;
        displayName: string;
        contentDisplayNameScript: string;
        superType: string;
        isAbstract: boolean;
        isFinal: boolean;
        allowChildContent: boolean;
        createdTime?: Date;
        modifiedTime?: Date;
        iconUrl: string;
        form: FormItem[];
    }

    export interface ContentTypeTreeNode extends ContentType {
        iconUrl:string;
        hasChildren:boolean;
        contentTypes:ContentTypeTreeNode[];
    }

    export interface ContentTypeListNode extends ContentType {
        iconUrl:string;
    }

    export interface MixinReference {
        name: string;
        reference: string;
        type: string;
    }

    export interface FormItem {
        FormItemSet?: FormItemSet;
        Layout?: Layout;
        Input?: Input;
        MixinReference?: MixinReference;
    }

    export interface FormItemSet {
        name: string;
        label: string;
        immutable: boolean;
        occurrences: Occurrences;
        customText: string;
        helpText: string;
        items: FormItem[];
    }

    export interface Layout {
        type: string;
        label: string;
        name: string;
        items: FormItem[];
    }

    export interface Input {
        name: string;
        label: string;
        immutable: boolean;
        occurrences: Occurrences;
        indexed: boolean;
        customText: string;
        validationRegexp?: string;
        helpText: string;
        config?: InputTypeConfig;
        type: InputType;
    }

    export interface Occurrences {
        minimum: number;
        maximum: number;
    }

    export interface InputType {
        name: string;
    }

    export interface InputTypeConfig {
        relationshipType?: string;
        selectorType?: string;
        options?: {
            label: string;
            value: string;
        }[];
    }

    export interface GetParams {
        names: string[];
        format: string;
        mixinReferencesToFormItems?: boolean;
    }

    export interface GetResult {
        contentTypes?: ContentType[];
        iconUrl?: string;
        contentTypeXmls?: string[];
    }

    export interface CreateOrUpdateParams {
        name: string;
        contentType: string;
        iconReference: string;
    }

    export interface CreateOrUpdateResult {
        created: boolean;
        updated: boolean;
        failure?: string;
    }

    export interface DeleteParams {
        names:string[];
    }

    export interface DeleteResult {
        successes:ContentTypeDeleteSuccess[];
        failures:ContentTypeDeleteFailure[];
    }

    export interface ContentTypeDeleteSuccess {
        name:string;

    }

    export interface ContentTypeDeleteFailure {
        name:string;
        reason:string;
    }

    export interface ListParams {
    }

    export interface ListResult{
        contentTypes:ContentTypeListNode[];
    }

    export interface GetTreeParams {
    }

    export interface GetTreeResult {
        total:number;
        contentTypes:ContentTypeTreeNode[];
    }

}