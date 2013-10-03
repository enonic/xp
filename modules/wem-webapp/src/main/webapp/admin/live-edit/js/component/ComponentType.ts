// fixme: Create interface for highlighterStyle?

interface HighlighterStyle {
    stroke:string;
    strokeDasharray:string;
    fill:string;
}

interface TypeConfiguration {
    type:LiveEdit.component.Type;
    typeName:string;
    cssSelector:string;
    draggable:boolean;
    cursor:string;
    iconCls:string;
    highlighterStyle?: HighlighterStyle;
    contextMenuConfig:string[];
}

module LiveEdit.component {

    export enum Type {
        PAGE,
        REGION,
        LAYOUT,
        PART,
        IMAGE,
        PARAGRAPH,
        CONTENT
    }

    export class ComponentType {

        private type:Type;

        private typeName:string;

        private cssSelector:string;

        private iconCls:string;

        private cursor:string;

        private highlighterStyle:HighlighterStyle;

        private contextMenuConfig:string[];

        constructor(type:Type) {

            var typeConfig:TypeConfiguration = LiveEdit.component.TypeConfiguration[type];

            this.setType(type);
            this.setName(typeConfig.typeName);
            this.setCssSelector(typeConfig.cssSelector);
            this.setIconCls(typeConfig.iconCls);
            this.setCursor(typeConfig.cursor);
            this.setHighlighterStyle(typeConfig.highlighterStyle);
            this.setContextMenuConfig(typeConfig.contextMenuConfig);
        }

        setType(type:Type):void {
            this.type = type;
        }

        getType():Type {
            return this.type;
        }

        setName(name:string):void {
            this.typeName = name;
        }

        getName():string {
            return this.typeName;
        }

        setCssSelector(selector:string):void {
            this.cssSelector = selector;
        }

        getCssSelector():string {
            return this.cssSelector;
        }

        setIconCls(cls:string):void {
            this.iconCls = cls;
        }

        getIconCls():string {
            return this.iconCls;
        }

        setCursor(cursor:string):void {
            this.cursor = cursor;
        }

        getCursor():string {
            return this.cursor;
        }

        setHighlighterStyle(styleConfig:HighlighterStyle):void {
            this.highlighterStyle = styleConfig;
        }

        getHighlighterStyle():HighlighterStyle {
            return this.highlighterStyle;
        }

        setContextMenuConfig(config:string[]):void {
            this.contextMenuConfig = config;
        }

        getContextMenuConfig():string[] {
            return this.contextMenuConfig;
        }

    }


    // fixme: refactor
    export var TypeConfiguration:TypeConfiguration[] = [
        {
            type: LiveEdit.component.Type.PAGE,
            typeName: 'page',
            cssSelector: '[data-live-edit-type=page]',
            draggable: false,
            cursor: 'pointer',
            iconCls: 'live-edit-font-icon-page',
            highlighterStyle: {
                stroke: '',
                strokeDasharray: '',
                fill: ''
            },
            contextMenuConfig: ['reset']
        },
        {
            type: LiveEdit.component.Type.REGION,
            typeName: 'region',
            cssSelector: '[data-live-edit-type=region]',
            draggable: false,
            cursor: 'pointer',
            iconCls: 'live-edit-font-icon-region',
            highlighterStyle: {
                stroke: 'rgba(20, 20, 20, 1)',
                strokeDasharray: '',
                fill: 'rgba(255, 255, 255, 0)'
            },
            contextMenuConfig: ['parent', 'reset', 'clear']

        },
        {
            type: LiveEdit.component.Type.LAYOUT,
            typeName: 'layout',
            cssSelector: '[data-live-edit-type=layout]',
            draggable: true,
            cursor: 'move',
            iconCls: 'live-edit-font-icon-layout',
            highlighterStyle: {
                stroke: 'rgba(255, 165, 0, 1)',
                strokeDasharray: '5 5',
                fill: 'rgba(100, 12, 36, 0)'
            },
            contextMenuConfig: ['parent', 'details', 'remove']
        },
        {
            type: LiveEdit.component.Type.PART,
            typeName: 'part',
            cssSelector: '[data-live-edit-type=part]',
            draggable: true,
            cursor: 'move',
            iconCls: 'live-edit-font-icon-part',
            highlighterStyle: {
                stroke: 'rgba(68, 68, 68, 1)',
                strokeDasharray: '5 5',
                fill: 'rgba(255, 255, 255, 0)'
            },
            contextMenuConfig: ['parent', 'details', 'remove']
        },
        {
            type: LiveEdit.component.Type.IMAGE,
            typeName: 'image',
            cssSelector: '[data-live-edit-type=image]',
            draggable: true,
            cursor: 'move',
            iconCls: 'live-edit-font-icon-image',
            highlighterStyle: {
                stroke: 'rgba(68, 68, 68, 1)',
                strokeDasharray: '5 5',
                fill: 'rgba(255, 255, 255, 0)'
            },
            contextMenuConfig: ['parent', 'details', 'remove']
        },
        {
            type: LiveEdit.component.Type.PARAGRAPH,
            typeName: 'paragraph',
            cssSelector: '[data-live-edit-type=paragraph]',
            draggable: true,
            cursor: 'move',
            iconCls: 'live-edit-font-icon-paragraph',
            highlighterStyle: {
                stroke: 'rgba(85, 85, 255, 1)',
                strokeDasharray: '5 5',
                fill: 'rgba(255, 255, 255, 0)'
            },
            contextMenuConfig: ['parent', 'edit', 'remove']
        },
        {
            type: LiveEdit.component.Type.CONTENT,
            typeName: 'content',
            cssSelector: '[data-live-edit-type=content]',
            draggable: false,
            cursor: 'pointer',
            iconCls: 'live-edit-font-icon-content',
            highlighterStyle: {
                stroke: '',
                strokeDasharray: '',
                fill: 'rgba(0, 108, 255, .25)'
            },
            contextMenuConfig: ['parent', 'opencontent', 'view']
        }
    ]
}

