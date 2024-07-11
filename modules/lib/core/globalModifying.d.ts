// NOTE: A global modifying module must have at least one export

export declare type ComponentDescriptor = `${string}:${string}`;

export declare interface NestedRecord {
	[name: PropertyKey]: NestedRecord | unknown
}

declare global {
    interface XpBeans {}
    interface XpLayoutMap {
        [layoutDescriptor: ComponentDescriptor]: NestedRecord;
    }
    interface XpLibraries {}
    interface XpPageMap {
        [pageDescriptor: ComponentDescriptor]: NestedRecord;
    }
    interface XpPartMap {
        [partDescriptor: ComponentDescriptor]: NestedRecord;
    }
    interface XpXData {
        [key: string]: Record<string, Record<string, unknown>>;
    }
}
