///<reference path='BaseResult.ts' />

module api_remote_contenttype {

    export interface ContentType extends api_remote.Item {
        name: string;
        module: string;
        qualifiedName?: string;
        displayName: string;
        contentDisplayNameScript: string;
        superType: string;
        isAbstract: bool;
        isFinal: bool;
        allowChildren: bool;
        createdTime?: Date;
        modifiedTime?: Date;
        iconUrl: string;
        form: FormItem[];
    }

    export interface ContentTypeTreeNode extends ContentType {
        iconUrl:string;
        hasChildren:bool;
        contentTypes:ContentTypeTreeNode[];
    }

    export interface ContentTypeListNode extends ContentType {
        iconUrl:string;
    }

    export interface FormItem {
        FormItemSet?: FormItemSet;
        Layout?: Layout;
        Input?: Input;
        MixinReference?: api_remote_mixin.MixinReference;
    }

    export interface FormItemSet {
        name: string;
        label: string;
        immutable: bool;
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
        immutable: bool;
        occurrences: Occurrences;
        indexed: bool;
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
        builtIn: bool;
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
        format: string;
        contentType: string;
        mixinReferencesToFormItems?: bool;
    }

    export interface GetResult extends api_remote.BaseResult {
        contentType?: ContentType;
        iconUrl?: string;
        contentTypeXml?: string;
    }

    export interface CreateOrUpdateParams {
        contentType: string;
        iconReference: string;
    }

    export interface CreateOrUpdateResult extends api_remote.BaseResult {
        created: bool;
        updated: bool;
        failure?: string;
    }

    export interface DeleteParams {
        qualifiedContentTypeNames:string[];
    }

    export interface DeleteResult extends api_remote.BaseResult {
        successes:ContentTypeDeleteSuccess[];
        failures:ContentTypeDeleteFailure[];
    }

    export interface ContentTypeDeleteSuccess {
        qualifiedContentTypeName:string;

    }

    export interface ContentTypeDeleteFailure {
        qualifiedContentTypeName:string;
        reason:string;
    }

    export interface ListParams {
    }

    export interface ListResult extends api_remote.BaseResult{
        contentTypes:ContentTypeListNode[];
    }

    export interface GetTreeParams {
    }

    export interface GetTreeResult extends api_remote.BaseResult {
        total:number;
        contentTypes:ContentTypeTreeNode[];
    }

}