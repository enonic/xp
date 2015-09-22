module api.liveedit {

    export interface HighlighterStyle {
        stroke:string;
        strokeDasharray:string;
        fill:string;
    }

    export interface ItemTypeConfigJson {
        typeName?:string;
        cssSelector:string;
        draggable:boolean;
        cursor:string;
        iconCls:string;
        highlighterStyle?: HighlighterStyle;
        contextMenuConfig:string[];
    }

    export class ItemTypeConfig {

        private typeName: string;
        private draggable: boolean;
        private cssSelector: string;
        private iconCls: string;
        private cursor: string;
        private highlighterStyle: HighlighterStyle;
        private contextMenuConfig: string[];

        constructor(json: ItemTypeConfigJson) {

            this.typeName = json.typeName;
            this.draggable = json.draggable;
            this.cssSelector = json.cssSelector;
            this.iconCls = json.iconCls;
            this.cursor = json.cursor;
            this.highlighterStyle = json.highlighterStyle;
            this.contextMenuConfig = json.contextMenuConfig;
        }

        getName(): string {
            return this.typeName;
        }

        isDraggable(): boolean {
            return this.draggable;
        }

        getCssSelector(): string {
            return this.cssSelector;
        }

        getIconCls(): string {
            return this.iconCls;
        }

        getCursor(): string {
            return this.cursor;
        }

        getHighlighterStyle(): HighlighterStyle {
            return this.highlighterStyle;
        }

        getContextMenuConfig(): string[] {
            return this.contextMenuConfig;
        }
    }

}

