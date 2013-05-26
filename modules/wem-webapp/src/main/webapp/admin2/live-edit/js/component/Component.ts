module LiveEdit {
    var componentHelper = LiveEdit.ComponentHelper;

    export class Component {

        private rootElement:JQuery;
        private type:string;
        private name:string;
        private key:string;
        private dimensions:ComponentBoxModel;
        private highlightStyle:HighlighterStyle;
        private iconCls:string;

        // fixme: move stuff from menu etc. into ComponentHelper
        private contextMenuConfig:any;

        constructor(element:JQuery) {
            this.setRootElement(element);
            this.setType(componentHelper.getComponentType(element));
            this.setName(componentHelper.getComponentName(element));
            this.setKey(componentHelper.getComponentKey(element));
            this.setDimensions(componentHelper.getBoxModel(element));
            this.setHighlightStyle(componentHelper.getHighlighterStyleForComponent(element));
            this.setIconCls(componentHelper.resolveCssClassForComponent(element));
        }

        getRootElement():JQuery {
            return this.rootElement;
        }

        setRootElement(element:JQuery):void {
            this.rootElement = element;
        }

        getType():string {
            return this.type;
        }

        setType(type:string):void {
            this.type = type;
        }

        getName():string {
            return this.name;
        }

        setName(name:string):void {
            this.name = name;
        }

        getKey():string {
            return this.key;
        }

        setKey(key:string):void {
            this.key = key;
        }

        getDimensions():ComponentBoxModel {
            return this.dimensions;
        }

        setDimensions(rectangle:ComponentBoxModel):void {
            this.dimensions = rectangle;
        }

        getHighlightStyle():HighlighterStyle {
            return this.highlightStyle;
        }

        setHighlightStyle(style:HighlighterStyle):void {
            this.highlightStyle = style;
        }

        getIconCls():any {
            return this.iconCls;
        }

        setIconCls(cls:string):void {
            this.iconCls = cls;
        }

        getContextMenuConfig():any {
            return this.contextMenuConfig;
        }

        setContextMenuConfig(config:any):void {
            this.contextMenuConfig = config;
        }

    }
}
