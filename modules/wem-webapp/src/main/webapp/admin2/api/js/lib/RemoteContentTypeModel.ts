module api_remote {

    export interface ContentType {
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
        form: FormItem[];
    }

    export interface SchemaTreeNode {
        key:string;
        name:string;
        module:string;
        qualifiedName:string;
        displayName:string;
        type:string;
        createdTime?:Date;
        modifiedTime?:Date;
        hasChildren:bool;
        schemas:SchemaTreeNode[];
    }

    export interface ContentTypeTreeNode extends ContentType {
        iconUrl:string;
        hasChildren:bool;
        contentTypes:ContentTypeTreeNode[];
    }

    export interface ContentTypeListNode extends ContentType {
        iconUrl:string;
    }

    export interface ContentTreeNode {
        allowsChildren:bool;
        contents:ContentTreeNode[];
        createdTime?:Date;
        deletable:bool;
        displayName:string;
        editable:bool;
        hasChildren:bool;
        iconUrl:string;
        id:string;
        modifiedTime?:Date;
        modifier:string;
        name:string;
        owner:string;
        path:string;
        type:string;
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

    export interface MixinReference {
        name: string;
        reference: string;
        type: string;
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

    export interface RelationshipType {
        name:string;
        displayName:string;
        module:string;
        fromSemantic:string;
        toSemantic:string;
        allowedFromTypes:string[];
        allowedToTypes:string[];
    }

    export interface Account {
        key:string;
        type:string;
        name:string;
        userStore:string;
        qualifiedName:string;
        builtIn:bool;
        displayName:string;
        modifiedTime?:Date;
        createdTime?:Date;
        editable:bool;
        deleted:bool;
        image_url:string;
        email?:string;
        profile?:UserProfile;
        memberships?:Account[];
        members?:Account[];
    }

    export interface UserProfile {
        firstName:string;
        lastName:string;
        middleName:string;
        birthday?:Date;
        country:string;
        description:string;
        initials:string;
        globalPosition:string;
        htmlEmail:string;
        locale?:string;
        nickName:string;
        personalId:string;
        memberId:string;
        organization:string;
        prefix:string;
        suffix:string;
        title:string;
        homePage:string;
        mobile:string;
        fax:string;
        phone:string;
        gender?:string;
        timezone?:string;
        addresses:Address[];
    }

    export interface Address {
        country:string;
        isoCountry:string;
        region:string;
        isoRegion:string;
        label:string;
        street:string;
        postalCode:string;
        postalAddress:string;
    }
}