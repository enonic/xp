module LiveEdit.component {
    var componentHelper = LiveEdit.component.ComponentHelper;

    export class Component {

        private jQueryElement:JQuery;
        private type:string;
        private name:string;
        private key:string;
        private dimensions:ComponentBoxModel;
        private highlightStyle:HighlighterStyle;
        private iconCls:string;

        // fixme: move context menu stuff here
        private contextMenuConfig:any;

        constructor(element:JQuery) {
            this.setJQueryElement(element);
            this.setType(componentHelper.getComponentType(element));
            this.setName(componentHelper.getComponentName(element));
            this.setKey(componentHelper.getComponentKey(element));
            this.setDimensions(componentHelper.getBoxModel(element));
            this.setHighlightStyle(componentHelper.getHighlighterStyleForComponent(element));
            this.setIconCls(componentHelper.resolveCssClassForComponent(element));
        }

        getJQueryElement():JQuery {
            return this.jQueryElement;
        }

        setJQueryElement(element:JQuery):void {
            this.jQueryElement = element;
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
