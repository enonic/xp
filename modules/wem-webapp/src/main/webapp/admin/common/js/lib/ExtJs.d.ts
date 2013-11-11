declare var Ext:Ext_Packages;


/**
 *      HTML dom element
 *      https://developer.mozilla.org/en-US/docs/DOM/element
 */
interface Html_dom_Element {

    id: string;
    name: string;
    innerHTML: string;
    style: string;
    title: string;
    className: string;
    value: string;
    parentElement: Html_dom_Element;
    children: Html_dom_Element[];
    src: string;

    hasAttribute(name:string):boolean;
    getAttribute(name:string):string;
    setAttribute(name:string, value:string);
    removeAttribute(name:string);
}


/**
 *      Ext global object, encapsulates all classes, singletons, and utility methods provided by Sencha's libraries.
 */
interface IExt {

    BLANK_IMAGE_URL: string;

    SSL_SECURE_URL: string;

    USE_NATIVE_JSON: boolean;

    chromeVersion: number;

    emptyFn: Function;

    emptyString: Object;

    enableFx: boolean;

    enableGarbageCollector: boolean;

    enableListenerCollection: boolean;

    enableNestedListenerRemoval: boolean;

    enumerables: string[];

    firefoxVersion : number;

    globalEvents: Ext_util_Observable;

    ieVersion: number;

    isChrome: boolean;

    isFF10: boolean;

    isFF3_0: boolean;

    isFF3_5: boolean;

    isFF3_6: boolean;

    isFF4: boolean;

    isFF5: boolean;

    isGecko: boolean;

    isGecko10: boolean;

    isGecko3: boolean;

    isGecko4: boolean;

    isGecko5: boolean;

    isIE: boolean;

    isIE6: boolean;

    isIE7: boolean;

    isIE7m: boolean;

    isIE7p: boolean;

    isIE8: boolean;

    isIE8m: boolean;

    isIE8p: boolean;

    isIE9: boolean;

    isIE9m: boolean;

    isIE9p: boolean;

    isLinux: boolean;

    isMac: boolean;

    isOpera: boolean;

    isOpera10_5: boolean;

    isReady: boolean;

    isSafari: boolean;

    isSafari2: boolean;

    isSafari3: boolean;

    isSafari4: boolean;

    isSafari5: boolean;

    isSafari5_0: boolean;

    isSecure: boolean;

    isWebKit: boolean;

    isWindows: boolean;

    name: string;

    operaVersion: number;

    resetCls: string;

    safariVersion: number;

    scopeResetCSS : boolean;

    useShims : boolean;

    webKitVersion : number;

    addBehaviors(obj:Object): void;

    application(config:Object): void;

    apply(target:any, config:any, defaults?:any): any;

    applyIf(target:Object, config:Object): Object;

    batchLayouts(fn:Function, scope?:Object): void;

    bind(fn:Function, scope?:Object, args?:any[], appendArgs?:boolean): Function;

    callback(callback:Function, scope?:Object, args?:any[], delay?:number): void;

    clone(item:any): any;

    copyTo(dest:Object, source:Object, names:string, usePrototypeKeys?:boolean): Object;
    copyTo(dest:Object, source:Object, names:string[], usePrototypeKeys?:boolean): Object;

    create(name:string, object?:Object): Object;
    create(object:Object): Object;

    createByAlias(alias:string, ...args:Object[]): Object;

    decode(value:string, safe?:boolean): Object;

    defer(fn:Function, millis:number, scope?:Object, args?:any[], appendArgs?:boolean): number;

    define(name:string, object:Object, callback?:Function): Ext_Base;

    deprecate(packageName:string, since:string, closure:Function, scope:Object): void;

    destroy(args:Ext_dom_Element): void;
    destroy(args:Ext_dom_Element[]): void;
    destroy(args:Ext_Component): void;
    destroy(args:Ext_Component[]): void;

    destroyMembers(o:Object, ...args:string[]): void;

    each(value:any, fn:(item:any, index:number, all:any) => any /*boolean*/, scope?:Object, reverse?:boolean);

    encode(value:any): string;

    exclude(excludes:any[]): Object;

    fly(node:string, named?:string): Ext_dom_Element_Fly;
    fly(node:Html_dom_Element, named?:string): Ext_dom_Element_Fly;

    get (node:string): Ext_dom_Element;
    get (node:Ext_dom_Element): Ext_dom_Element;
    get (node:Html_dom_Element): Ext_dom_Element;
    get (node:HTMLElement): Ext_dom_Element;

    getBody(): Ext_dom_Element;

    getClass(object:Object): Ext_Class;

    getClassName(object:Object): string;
    getClassName(object:Ext_Class): string;

    getCmp(id:string): Ext_Component;

    getDoc(): Ext_dom_Element;

    getDom(el:string): Html_dom_Element;
    getDom(el:Html_dom_Element): Html_dom_Element;
    getDom(el:Ext_dom_Element): Html_dom_Element;

    getHead(): Ext_dom_Element;

    getOrientation(): string;

    getScrollbarSize(force?:boolean): {
        width: number;
        height: number;
    };

    getStore(store:string): Ext_data_Store;
    getStore(store:Object): Ext_data_Store;
    getStore(store:Ext_data_Store): Ext_data_Store;

    getVersion(packageName?:string): Ext_Version;

    id(el?:Html_dom_Element, prefix?:string): string;
    id(el?:Ext_dom_Element, prefix?:string): string;

    identityFn(o:Object): any;      // ?

    isArray(value:any): boolean;

    isboolean(value:any): boolean;

    isDate(value:any): boolean;

    isDefined(value:any): boolean;

    isElement(value:any): boolean;

    isEmpty(value:any, allowEmptyString?:boolean): boolean;

    isFunction(value:any): boolean;

    isIterable(value:any): boolean;

    isNumber(value:any): boolean;

    isNumeric(value:any): boolean;

    isObject(value:any): boolean;

    isPrimitive(value:any): boolean;

    isString(value:any): boolean;

    isTextNode(value:any): boolean;

    iterate(o:Object, fn:(item:Object, index:number, all:any[]) => boolean, scope?:Object): void;

    log(options:string): void;
    log(options:{
        msg: string;
        level?: string;
        dump?: Object;
        stack?: boolean;
        indent?: boolean;
        outdent?: boolean;
    }): void;

    merge(dest:Object, object:Object): Object;

    namespace(...namespaces:string[]): Object;

    ns(...namespaces:string[]): Object;

    onDocumentReady(fn:Function, scope?:Object, options?:Object): void;

    onReady(fn:Function, scope?:Object, options?:Object): void;

    override(target:Object, overrides:Object): void;

    pass(fn:Function, args:any[], scope:Object): Function;

    preg(ptype:string, cls:Function): void;

    query(path:string, root?:Html_dom_Element, type?:string) : Html_dom_Element[];

    regStore(id:string, config:Object): void;

    removeNode(node:Html_dom_Element);

    require(expressions:string, callback?:Function, scope?:Object, excludes?:any /* String/Srray */);
    require(expressions:any[], callback?:Function, scope?:Object, excludes?:any /* String/Srray */);

    resumeLayouts(flush:Object): void;

    select(selector:string, unique?:boolean, root?:string): Ext_dom_CompositeElement;
    select(selector:string, unique?:boolean, root?:Html_dom_Element): Ext_dom_CompositeElement;
    select(selector:Html_dom_Element[], unique?:boolean, root?:string): Ext_dom_CompositeElement;
    select(selector:Html_dom_Element[], unique?:boolean, root?:Html_dom_Element): Ext_dom_CompositeElement;

    setVersion(packageName:string, version:string) : IExt;
    setVersion(packageName:string, version:Ext_Version) : IExt;

    suspendLayouts(): void;

    syncRequire(expressions:string, fn?:Function, scope?:Object, excludes?:any /* String/Array */): void;
    syncRequire(expressions:any[], fn?:Function, scope?:Object, excludes?:any /* String/Array */): void;

    toArray(iterable:Object, start?:number, end?:number): any[];

    typeOf(value:any): string;

    valueFrom(value:Object, defaultValue:Object, allowBlank?:boolean): Object;

    widget(name?:string, config?:Object): Object;
}


interface Ext_Packages extends IExt {

    // managers
    ClassManager: Ext_ClassManager;
    EventManager: Ext_EventManager;
    WindowManager: Ext_WindowManager;
    StoreManager: Ext_data_StoreManager;
    Direct: Ext_direct_Manager;

    // component
    Component: Ext_Component;
    ComponentLoader: Ext_ComponentLoader;
    ComponentManager: Ext_ComponentManager;
    ComponentQuery: Ext_ComponentQuery;

    // element
    Element: Ext_dom_Element;
    ElementLoader: Ext_ElementLoader;
    DomHelper: Ext_dom_Helper;
    DomQuery: Ext_dom_Query;

    Template: Ext_Template;
    LoadMask: Ext_LoadMask;
    XTemplate: Ext_XTemplate;
    Loader: Ext_Loader;

    Array: Ext_Array;
    String: Ext_String;
    Function: Ext_Function;
    Object: Ext_Object;
    JSON: Ext_JSON;

    Img: Ext_Img;
    Msg: Ext_window_MessageBox;
    MessageBox: Ext_window_MessageBox;


    app: {
        Application: Ext_app_Application;
        Controller: Ext_app_Controller;
    };
    button: {
        Button: Ext_button_Button;
        Split: Ext_button_Split;
        Cycle: Ext_button_Cycle;
    };
    container: {
        ButtonGroup: Ext_container_ButtonGroup;
        Container: Ext_container_Container;
        DockingContainer: Ext_container_DockingContainer;
        Viewport: Ext_container_Viewport;
    };
    data: {
        proxy: {
            Proxy: Ext_data_proxy_Proxy;
            Rest: Ext_data_proxy_Rest;
        };
        writer: {
            Writer: Ext_data_writer_Writer;
        };
        reader: {
            Reader: Ext_data_reader_Reader;
        };
        AbstractStore: Ext_data_AbstractStore;
        Batch: Ext_data_Batch;
        Errors: Ext_data_Errors;
        Operation: Ext_data_Operation;
        Store: Ext_data_Store;
        TreeStore: Ext_data_TreeStore;
        Model: Ext_data_Model;
        StoreManager: Ext_data_StoreManager;

    };
    dd: {
        DD: Ext_dd_DD;
        DDProxy: Ext_dd_DDProxy;
        DDTarget: Ext_dd_DDTarget;
        DragSource: Ext_dd_DragSource;
        DragZone: Ext_dd_DragZone;
        DropTarget: Ext_dd_DropTarget;
        DropZone: Ext_dd_DropZone;
        ScrollManager: Ext_dd_ScrollManager;
        StatusProxy: Ext_dd_StatusProxy;
    };
    direct: {
        RemotingMethod: Ext_direct_RemotingMethod;
        Provider: Ext_direct_Provider;
        RemotingProvider: Ext_direct_RemotingProvider;
        JsonProvider: Ext_direct_JsonProvider;
        Event: Ext_direct_Event;
        Transaction: Ext_direct_Transaction;
        Manager: Ext_direct_Manager;
    };
    dom: {
        Element: Ext_dom_Element;
        CompositeElementLite: Ext_dom_CompositeElementLite;
        CompositeElement: Ext_dom_CompositeElement;
        Query: Ext_dom_Query;
        Helper: Ext_dom_Helper;
    };
    form: {
        field: {
            VTypes: Ext_form_field_VTypes;
            Base: Ext_form_field_Base;
            Text: Ext_form_field_Text;
            Display: Ext_form_field_Display;
            Checkbox: Ext_form_field_Checkbox;
            Trigger: Ext_form_field_Trigger;
            Picker: Ext_form_field_Picker;
            ComboBox: Ext_form_field_ComboBox;
        };
        Panel: Ext_form_Panel;
        FieldSet: Ext_form_FieldSet;
    };
    grid: {
        Panel: Ext_grid_Panel;
        View: Ext_grid_View;
    };
    layout: {
        component: {
            Auto: Ext_layout_component_Auto;
        };
        container: {
            Container: Ext_layout_container_Container;
            Border: Ext_layout_container_Border;
            Fit: Ext_layout_container_Fit;
            Card: Ext_layout_container_Card;
            VBox: Ext_layout_container_VBox;
            HBox: Ext_layout_container_HBox;
        };
        Layout: Ext_layout_Layout;
    };
    menu: {
        Menu: Ext_menu_Menu;
        Item: Ext_menu_Item;
        CheckItem: Ext_menu_CheckItem;
    };
    panel: {
        Panel: Ext_panel_Panel;
    };
    tab: {
        Panel: Ext_tab_Panel;
    };
    toolbar: {
        Toolbar: Ext_toolbar_Toolbar;
        Fill: Ext_toolbar_Fill;
    };
    tree: {
        Panel: Ext_tree_Panel;
        View: Ext_tree_View;
    };
    util: {
        Animate: Ext_util_Animate;
        Cookies: Ext_util_Cookies;
        Renderable: Ext_util_Renderable;
        Observable: Ext_util_Observable;
        Floating: Ext_util_Floating;
        Filter: Ext_util_Filter;
        Offset: Ext_util_Offset;
        Point: Ext_util_Point;
        Region: Ext_util_Region;
        TaskRunner: Ext_util_TaskRunner;
    };
    window: {
        Window: Ext_window_Window;
        MessageBox: Ext_window_MessageBox;
    };
}


/**
 *      Handles class creation throughout the framework.
 */
interface Ext_Class {

    new(data:Object, onCreated:Function): Ext_Base;

}


/**
 *      The root of all classes created with Ext.define.
 */
interface Ext_Base {

    getInitialConfig(name?:string): any;      // Object/Mixed

    addMembers(members): void;

    addStatics(members): Ext_Base;

    create(): Object;

    createAlias(alias, origin): void;

    getName(): string;

}


/*      DOM package     */


interface Ext_dom_AbstractElement extends Ext_Base {

    ASCLASS: number;
    DISPLAY: number;
    OFFSETS: number;
    VISIBILITY: number;

    defaultUnit: string;
    dom: Html_dom_Element;
    id: string;

    addCls(name:string): Ext_dom_Element;

    appendChild(el:string): Ext_dom_Element;
    appendChild(el:Html_dom_Element): Ext_dom_Element;
    appendChild(el:Ext_dom_Element): Ext_dom_Element;

    appendTo(el:string): Ext_dom_Element;
    appendTo(el:Html_dom_Element): Ext_dom_Element;
    appendTo(el:Ext_dom_Element): Ext_dom_Element;

    applyStyles(styles:string): Ext_dom_Element;
    applyStyles(styles:Object): Ext_dom_Element;
    applyStyles(styles:Function): Ext_dom_Element;

    child(selector:string, returnDom?:boolean): any;     // HTMLElement/Ext.dom.Element

    contains(el:string):boolean;
    contains(el:Html_dom_Element):boolean;

    createChild(config:Object, insertBefore?:Html_dom_Element, returnDom?:boolean): any;  // HTMLElement/Ext.dom.Element;

    destroy(): void;

    down(selector:string, returnDom?:boolean): any;     // HTMLElement/Ext.dom.Element

    findParent(selector:string, limit?:number, returnEl?:boolean): any;  // HTMLElement/Ext.dom.Element;
    findParent(selector:string, limit?:string, returnEl?:boolean): any;  // HTMLElement/Ext.dom.Element;
    findParent(selector:string, limit?:Html_dom_Element, returnEl?:boolean): any;  // HTMLElement/Ext.dom.Element;
    findParent(selector:string, limit?:Ext_dom_Element, returnEl?:boolean): any;  // HTMLElement/Ext.dom.Element;

    findParentNode(selector:string, limit?:number, returnEl?:boolean): any;    // HTMLElement/Ext.dom.Element
    findParentNode(selector:string, limit?:string, returnEl?:boolean): any;    // HTMLElement/Ext.dom.Element
    findParentNode(selector:string, limit?:Html_dom_Element, returnEl?:boolean): any;    // HTMLElement/Ext.dom.Element
    findParentNode(selector:string, limit?:Ext_dom_Element, returnEl?:boolean): any;    // HTMLElement/Ext.dom.Element

    first(selector?:string, returnDom?:boolean): any;   //Ext.dom.Element/HTMLElement

    getActiveElement(): Html_dom_Element;

    getAlignToXY(element:string, position?:string, offsets?:number[]): number[];
    getAlignToXY(element:Html_dom_Element, position?:string, offsets?:number[]): number[];
    getAlignToXY(element:Ext_dom_Element, position?:string, offsets?:number[]): number[];

    getAnchorXY(anchor?:string, local?:boolean, size?:Object): number[];

    getAttribute(name:string, namespace?:string): string;

    getBorderWidth(side:string): number;

    getBottom(local:boolean): number;

    getBox(contentBox?:boolean, local?:boolean): {
        x: number;
        y: number;
        width: number;
        height: number;
        bottom: number;
        right: number;
    };

    getById(id:string, asDom?:boolean): any;   // HTMLElement/Ext.dom.Element

    getHTML(): string;

    getHeight(contentHeight?:boolean): number;

    getLeft(local:boolean): number;

    getMargin(sides?:string): any;    // Object/Number

    getOffsetsTo(element:string): number[];
    getOffsetsTo(element:Html_dom_Element): number[];
    getOffsetsTo(element:Ext_dom_Element): number[];

    getPadding(side:string): number;

    getPageBox(asRegion?:boolean): {
        left: number;
        top: number;
        width: number;
        height: number;
        bottom: number;
        right: number;
    };

    getRight(local:boolean): number;

    getSize(contentSize?:boolean) : {
        width: number;
        height: number;
    };

    getStyle(property:string, inline:boolean): string;
    getStyle(property:string[], inline:boolean): Object;

    getTop(local:boolean): number;

    getValue(asNumber:boolean) : any;   // String/Number;

    getViewSize() : {
        width: number;
        height: number;
    };

    getWidth(contentWidth?:boolean) : number;

    getX(el:Object): number;

    getXY(): number[];

    getY(el:Object): number[];

    hasCls(name:string): boolean;

    hide(animate?:boolean): Ext_dom_Element;

    insertAfter(el:string): Ext_dom_AbstractElement;
    insertAfter(el:Html_dom_Element): Ext_dom_AbstractElement;
    insertAfter(el:Ext_dom_AbstractElement): Ext_dom_AbstractElement;

    insertBefore(el:string): Ext_dom_AbstractElement;
    insertBefore(el:Html_dom_Element): Ext_dom_AbstractElement;
    insertBefore(el:Ext_dom_AbstractElement): Ext_dom_AbstractElement;

    insertFirst(el:string): Ext_dom_AbstractElement;
    insertFirst(el:Html_dom_Element): Ext_dom_AbstractElement;
    insertFirst(el:Ext_dom_AbstractElement): Ext_dom_AbstractElement;

    insertHtml(where:string, html:string, returnEl?:boolean): any;     // HTMLElement/Ext_dom_AbstractElement

    insertSibling(el:string, where?:string, returnDom?:boolean): Ext_dom_AbstractElement;
    insertSibling(el:Html_dom_Element, where?:string, returnDom?:boolean): Ext_dom_AbstractElement;
    insertSibling(el:Ext_dom_AbstractElement, where?:string, returnDom?:boolean): Ext_dom_AbstractElement;
    insertSibling(el:Object, where?:string, returnDom?:boolean): Ext_dom_AbstractElement;
    insertSibling(el:Object[], where?:string, returnDom?:boolean): Ext_dom_AbstractElement;

    is(selector:string): boolean;

    isStyle(style:string, value:string): boolean;

    isTransparent(property:string): boolean;

    last(selector?:string, returnDom?:boolean): any;       // Ext.dom.Element/HTMLElement

    mask(msg?:string, msgCls?:string): void;

    next(selector?:string, returnDom?:boolean) : any;      // Ext.dom.Element/HTMLElement

    parent(selector?:string, returnDom?:boolean): any;     // Ext.dom.Element/HTMLElement

    populateStyleMap(map:Object, order:Object): void;

    prev(selector?:string, returnDom?:boolean): any;       // Ext.dom.Element/HTMLElement

    query(selector:string): Html_dom_Element[];

    radioCls(className:string): Ext_dom_Element;
    radioCls(className:string[]): Ext_dom_Element;

    remove(): void;

    removeCls(className:string): Ext_dom_Element;
    removeCls(className:string[]): Ext_dom_Element;

    repaint(): Ext_dom_Element;

    replace(el:string) : Ext_dom_AbstractElement;
    replace(el:Html_dom_Element) : Ext_dom_AbstractElement;
    replace(el:Ext_dom_AbstractElement) : Ext_dom_AbstractElement;

    replaceCls(oldClassName:string, newClassName:string): Ext_dom_Element;

    replaceWith(el:string): Ext_dom_AbstractElement;
    replaceWith(el:Html_dom_Element): Ext_dom_AbstractElement;
    replaceWith(el:Ext_dom_Element): Ext_dom_AbstractElement;
    replaceWith(el:Object): Ext_dom_AbstractElement;

    select(selector:string, unique?:boolean): Ext_dom_CompositeElement;

    serializeForm(form:Object): String;

    set(obj:Object, useSet?:boolean): Ext_dom_Element;

    setBottom(bottom:string): Ext_dom_AbstractElement;

    setBox(box:{
        x: number;
        y: number;
        width: number;
        height: number;
    }, adjust?:boolean, animate?:any /* boolean/Object */): Ext_dom_AbstractElement;

    setHTML(html:string): Ext_dom_Element;

    setHeight(height:number, animate:any /* boolean/Object */): Ext_dom_Element;
    setHeight(height:string, animate:any /* boolean/Object */): Ext_dom_Element;

    setLeft(left:string): Ext_dom_AbstractElement;

    setRight(right:string): Ext_dom_AbstractElement;

    setSize(width:number, height:number, animate?:any /* boolean/Object */): Ext_dom_Element;
    setSize(width:string, height:string, animate?:any /* boolean/Object */): Ext_dom_Element;
    setSize(width:{
        width: number;
        height: number;
    }, animate?:any /* boolean/Object */): Ext_dom_Element;

    setStyle(property:string, value:string): Ext_dom_Element;
    setStyle(property:string, value:number): Ext_dom_Element;
    setStyle(property:Object): Ext_dom_Element;

    setTop(top:string): Ext_dom_AbstractElement;

    setVisibilityMode(mode:number): Ext_dom_AbstractElement;

    setVisible(visible:boolean, animate?:any /* boolean/Object */): Ext_dom_Element;

    setWidth(width:number, animate?:any /* boolean/Object */): Ext_dom_Element;
    setWidth(width:string, animate?:any /* boolean/Object */): Ext_dom_Element;

    setX(x:number, animate?:any /* boolean/Object */): Ext_dom_AbstractElement;

    setXY(pos:number[], animate?:boolean): Ext_dom_AbstractElement;

    setY(y:number, animate?:any /* boolean/Object */): Ext_dom_AbstractElement;

    show(animate?:any /* boolean/Object */): Ext_dom_Element;

    toggleCls(className:string): Ext_dom_Element;

    translatePoints(x:number, y:number): {
        left: number;
        top: number;
    };
    translatePoints(x:number[]): {
        left: number;
        top: number;
    };

    unmask(): void;

    up(selector:string, limit?:any /* Number/String/HTMLElement/Ext.Element */): Ext_dom_Element;

    update(html:string): Ext_dom_Element;

    wrap(config?:Object, returnDom?:boolean, selector?:string): any;      // HTMLElement/Ext_dom_AbstractElement

}


interface Ext_dom_Element_ListenerOptions {
    scope?: Object;
    delegate?: string;
    stopEvent?: boolean;
    preventDefault?: boolean;
    stopPropagation?: boolean;
    normalized?: boolean;
    target?: Ext_dom_Element;
    delay?: number;
    single?: boolean;
    buffer?: number;
}


/**
 *      Encapsulates a DOM element, adding simple DOM manipulation facilities, normalizing for browser differences.
 */
interface Ext_dom_Element extends Ext_dom_AbstractElement {

    autoBoxAdjust: boolean;
    originalDisplay: string;

    new(el:Html_dom_Element, forceNew?:boolean): Ext_dom_Element;
    new(el:string, forceNew?:boolean): Ext_dom_Element;

    addClsOnClick(className:string, testFn?:Function, scope?:Object): Ext_dom_Element;

    addClsOnFocus(className:string, testFn?:Function, scope?:Object): Ext_dom_Element;

    addClsOnOver(className:string, testFn?:Function, scope?:Object): Ext_dom_Element;

    addKeyListener(key:string, fn:Function, scope?:Function): Ext_util_KeyMap;
    addKeyListener(key:number, fn:Function, scope?:Function): Ext_util_KeyMap;
    addKeyListener(key:number[], fn:Function, scope?:Function): Ext_util_KeyMap;
    addKeyListener(key:{
        key: any;       // Number/Array
        shift: boolean;
        ctrl: boolean;
        alt: boolean;
    }, fn:Function, scope?:Function): Ext_util_KeyMap;

    addKeyMap(config:Object): Ext_util_KeyMap;

    addListener(eventName:string, fn:(evt:Object, el:Html_dom_Element, opts:Object) => any, scope?:Object,
                options?:Ext_dom_Element_ListenerOptions): Ext_dom_Element;

    alignTo(element:string, position?:string, offsets?:number[], animate?:any /* boolean/Object */): Ext_dom_Element;
    alignTo(element:Html_dom_Element, position?:string, offsets?:number[], animate?:any /* boolean/Object */): Ext_dom_Element;
    alignTo(element:Ext_dom_Element, position?:string, offsets?:number[], animate?:any /* boolean/Object */): Ext_dom_Element;

    anchorTo(element:string, position:string, offsets?:number[], animate?:any /* boolean/Object */, monitorScroll?:any /* boolean/Number */,
             callback?:Function): Ext_dom_Element;
    anchorTo(element:Html_dom_Element, position:string, offsets?:number[], animate?:any /* boolean/Object */, monitorScroll?:any
             /* boolean/Number */, callback?:Function): Ext_dom_Element;
    anchorTo(element:Ext_dom_Element, position:string, offsets?:number[], animate?:any /* boolean/Object */, monitorScroll?:any
             /* boolean/Number */, callback?:Function): Ext_dom_Element;

    animate(config:Object): Ext_dom_Element;

    blur(): Ext_dom_Element;

    boxWrap(cls?:string): Ext_dom_Element;

    cacheScrollValues(): Function;

    center(centerIn?:string): void;
    center(centerIn?:Html_dom_Element): void;
    center(centerIn?:Ext_dom_Element): void;

    clean(forceReclean?:boolean): void;

    clearListeners(): Ext_dom_Element;

    clearOpacity(): Ext_dom_Element;

    clearPositioning(value?:string): Ext_dom_AbstractElement;

    clip(): Ext_dom_Element;

    createProxy(config:string, renderTo?:string, matchBox?:boolean): Ext_dom_Element;
    createProxy(config:string, renderTo?:Html_dom_Element, matchBox?:boolean): Ext_dom_Element;
    createProxy(config:Object, renderTo?:string, matchBox?:boolean): Ext_dom_Element;
    createProxy(config:Object, renderTo?:Html_dom_Element, matchBox?:boolean): Ext_dom_Element;

    createShim(): Ext_dom_Element;

    enableDisplayMode(display?:string): Ext_dom_Element;

    fadeIn(options?:Object): Ext_dom_Element;

    fadeOut(options?:Object): Ext_dom_Element;

    focus(defer?:number): Ext_dom_Element;

    focusable(asFocusEl:Object): boolean;

    frame(color?:string, count?:number, options?:Object): Ext_dom_Element;

    getAttributeNS(namespace:string, name:string): string;

    getCenterXY(): number[];

    getColor(attr:string, defaultValue:string, prefix?:string): void;

    getComputedHeight(): number;

    getComputedWidth(): number;

    getConstrainVector(constrainTo:Ext_dom_Element, proposedPosition:number[]) : any;        // number[]/boolean
    getConstrainVector(constrainTo:Ext_util_Region, proposedPosition:number[]) : any;        // number[]/boolean

    getFrameWidth(sides:string): number;

    getLoader(): Ext_ElementLoader;

    getLocalX(): number;

    getLocalY(): number;

    getPositioning(): Object;

    getRegion(): Ext_util_Region;

    getScroll(): Object;

    getStyleSize(): Object;

    getTextWidth(text:string, min?:number, max?:number): number;

    getViewRegion(): Ext_util_Region;

    ghost(anchor?:string, options?:Object): Ext_dom_Element;

    highlight(color:string, options?:Object): Ext_dom_Element;

    hover(overFn:Function, outFn:Function, scope?:Object, options?:Object): Ext_dom_Element;

    initDD(group, config, overrides): Ext_dd_DD;

    initDDProxy(group:string, config:Object, overrides:Object): Ext_dd_DDProxy;

    initDDTarget(group:string, config:Object, overrides:Object): Ext_dd_DDTarget;

    isBorderBox(): boolean;

    isDisplayed(): boolean;

    isFocusable(asFocusEl:Object): boolean;

    isMasked(): boolean;

    isScrollable(): boolean;

    isVisible(deep?:boolean): boolean;

    load(options:Object): Ext_dom_Element;

    monitorMouseLeave(delay:number, handler:Function, scope?:Object): Object;

    move(direction:string, distance:number, animate:any /* boolean/Object */): void;

    moveTo(x:number, y:number, animate:any /* boolean/Object */) : Ext_dom_AbstractElement;

    needsTabIndex(): void;

    on(eventName:string, fn:(evt:Object, el:Html_dom_Element, opts:Object) => any, scope?:Object,
       options?:Ext_dom_Element_ListenerOptions): Ext_dom_Element;

    position(pos?:string, zIndex?:number, x?:number, y?:number): void;

    puff(options:Object): Ext_dom_Element;

    purgeAllListeners(): Ext_dom_Element;

    relayEvent(eventName:string, observable:Object): void;

    removeAllListeners(): Ext_dom_Element;

    removeAnchor(): Ext_dom_Element;

    removeListener(eventName:string, fn:Function, scope:Object): Ext_dom_Element;

    scroll(direction:string, distance:number, animate?:any /* boolean/Object */): boolean;

    scrollBy(deltaX:number, deltaY:number, animate?:any /* boolean/Object */): Ext_dom_Element;
    scrollBy(deltaX:number[], animate?:any /* boolean/Object */): Ext_dom_Element;
    scrollBy(deltaX:Object, animate?:any /* boolean/Object */): Ext_dom_Element;

    scrollIntoView(container:string, hscroll?:boolean, animate?:any /* boolean/Object */): Ext_dom_Element;
    scrollIntoView(container:Html_dom_Element, hscroll?:boolean, animate?:any /* boolean/Object */): Ext_dom_Element;
    scrollIntoView(container:Ext_dom_Element, hscroll?:boolean, animate?:any /* boolean/Object */): Ext_dom_Element;

    scrollTo(side:string, value:number, animate?:any): Ext_dom_Element;

    selectable(): Ext_dom_Element;

    setBounds(x:number, y:number, width:number, height:number, animate?:any /* boolean/Object */): Ext_dom_AbstractElement;
    setBounds(x:number, y:number, width:string, height:string, animate?:any /* boolean/Object */): Ext_dom_AbstractElement;

    setDisplayed(value:boolean) : Ext_dom_Element;
    setDisplayed(value:String) : Ext_dom_Element;

    setLeftTop(left:string, top:string): Ext_dom_Element;

    setLocation(x:number, y:number, animate?:any /* boolean/Object */): Ext_dom_AbstractElement;

    setOpacity(opacity:number, animate?:any /* boolean/Object */): Ext_dom_Element;

    setPositioning(posCfg:Object): Ext_dom_AbstractElement;

    setRegion(region:Ext_util_Region, animate?:any /* boolean/Object */): Ext_dom_AbstractElement;

    slideIn(anchor?:string, options?:Object): Ext_dom_Element;

    slideOut(anchor?:string, options?:Object): Ext_dom_Element;

    swallowEvent(eventName:string, preventDefault?:boolean): Ext_dom_Element;
    swallowEvent(eventName:string[], preventDefault?:boolean): Ext_dom_Element;

    switchOff(options?:Object): Ext_dom_Element;

    toggle(animate?:any /* boolean/Object */): Ext_dom_Element;

    un(eventName:string, fn:Function, scope:Object): Ext_dom_Element;

    unclip(): Ext_dom_Element;

    unselectable(): Ext_dom_Element;

}


/**
 *      A non-persistent wrapper for a DOM element which may be used to
 *      execute methods of Ext.dom.Element upon a DOM element without creating an instance of Ext.dom.Element.
 */
interface Ext_dom_Element_Fly extends Ext_dom_Element {

    isFly: boolean;

}


/**
 *      This class encapsulates a collection of DOM elements, providing methods to filter members,
 *      or to perform collective actions upon the whole set.
 */
interface Ext_dom_CompositeElementLite extends Ext_Base {

    isComposite: boolean;
    elements: Html_dom_Element[];

    add(els:Html_dom_Element[]): Ext_dom_CompositeElement;
    add(els:Ext_dom_CompositeElement): Ext_dom_CompositeElement;

    clear(): void;

    contains(el:string): boolean;
    contains(el:number): boolean;
    contains(el:Html_dom_Element): boolean;
    contains(el:Ext_dom_Element): boolean;

    each(fn:(el:Ext_dom_Element, all:Ext_dom_CompositeElement, index:number) => boolean, scope:Object): Ext_dom_CompositeElement;

    fill(els:Html_dom_Element[]): Ext_dom_CompositeElement;
    fill(els:Ext_dom_CompositeElement): Ext_dom_CompositeElement;

    filter(selector:string): Ext_dom_CompositeElement;
    filter(selector:(el:Ext_dom_Element, index:number) => any): Ext_dom_CompositeElement;

    first(): Ext_dom_Element;

    getCount(): number;

    indexOf(el:string): number;
    indexOf(el:number): number;
    indexOf(el:Html_dom_Element): number;
    indexOf(el:Ext_dom_Element): number;

    item(index:number): Ext_dom_Element;

    last(): Ext_dom_Element;

    removeElement(el:string, removeDom?:boolean): Ext_dom_CompositeElement;
    removeElement(el:number, removeDom?:boolean): Ext_dom_CompositeElement;
    removeElement(el:Html_dom_Element, removeDom?:boolean): Ext_dom_CompositeElement;
    removeElement(el:Ext_dom_Element, removeDom?:boolean): Ext_dom_CompositeElement;

    replaceElement(el:string, replacement:any /* String/Ext.Element */, domReplace?:boolean): Ext_dom_CompositeElement;
    replaceElement(el:number, replacement:any /* String/Ext.Element */, domReplace?:boolean): Ext_dom_CompositeElement;
    replaceElement(el:Html_dom_Element, replacement:any /* String/Ext.Element */, domReplace?:boolean): Ext_dom_CompositeElement;
    replaceElement(el:Ext_dom_Element, replacement:any /* String/Ext.Element */, domReplace?:boolean): Ext_dom_CompositeElement;

}


/**
 *      This class encapsulates a collection of DOM elements, providing methods to filter members,
 *      or to perform collective actions upon the whole set.
 */
interface Ext_dom_CompositeElement extends Ext_dom_CompositeElementLite {

    // nothing here

}


/**
 *      The DomHelper class provides a layer of abstraction from DOM
 *      and transparently supports creating elements via DOM or using HTML fragments.
 */
interface Ext_dom_Helper extends Ext_Base {

    useDom: boolean;

    append(el:string, obj:any /* Object/String */, returnElement?:boolean): any;      // HTMLElement/Ext.Element
    append(el:Html_dom_Element, obj:any /* Object/String */, returnElement?:boolean): any;   // HTMLElement/Ext.Element
    append(el:Ext_dom_Element, obj:any /* Object/String */, returnElement?:boolean): any;    // HTMLElement/Ext.Element

    applyStyles(el:string, styles:any /* String/Object/Function */);
    applyStyles(el:Html_dom_Element, styles:any /* String/Object/Function */);

    createDom(obj:string): Html_dom_Element;
    createDom(obj:Object): Html_dom_Element;

    createHtml(spec:Object): string;

    createTemplate(obj:Object): Ext_Template;

    generateStyles(styles:Object, buffer?:string[]): any;       // String/String[]

    insertAfter(el:string, obj:any /* Object/String */, returnElement?:boolean): any;        // HTMLElement/Ext.Element
    insertAfter(el:Html_dom_Element, obj:any /* Object/String */, returnElement?:boolean): any;        // HTMLElement/Ext.Element
    insertAfter(el:Ext_dom_Element, obj:any /* Object/String */, returnElement?:boolean): any;        // HTMLElement/Ext.Element

    insertBefore(el:string, obj:any /* Object/String */, returnElement?:boolean): any;        // HTMLElement/Ext.Element
    insertBefore(el:Html_dom_Element, obj:any /* Object/String */, returnElement?:boolean): any;        // HTMLElement/Ext.Element
    insertBefore(el:Ext_dom_Element, obj:any /* Object/String */, returnElement?:boolean): any;        // HTMLElement/Ext.Element

    insertFirst(el:string, obj:any /* Object/String */, returnElement?:boolean): any;        // HTMLElement/Ext.Element
    insertFirst(el:Html_dom_Element, obj:any /* Object/String */, returnElement?:boolean): any;        // HTMLElement/Ext.Element
    insertFirst(el:Ext_dom_Element, obj:any /* Object/String */, returnElement?:boolean): any;        // HTMLElement/Ext.Element

    insertHtml(where:string, el:Html_dom_Element, html:string): Html_dom_Element;

    markup(spec:Object): string;

    overwrite(el:string, obj:any /* Object/String */, returnElement?:boolean): any;       // HTMLElement/Ext.Element
    overwrite(el:Html_dom_Element, obj:any /* Object/String */, returnElement?:boolean): any;       // HTMLElement/Ext.Element
    overwrite(el:Ext_dom_Element, obj:any /* Object/String */, returnElement?:boolean): any;       // HTMLElement/Ext.Element

}


/**
 *      Provides high performance selector/xpath processing by compiling queries into reusable functions.
 */
interface Ext_dom_Query extends Ext_Base {

    matchers: Object[];

    operators: Object;

    pseudos: Object;

    compile(selector:string, type?:string): Function;

    filter(el:Html_dom_Element[], selector:string, nonMatches:boolean) : Html_dom_Element[];

    is(el:string, selector:string): boolean;
    is(el:Html_dom_Element, selector:string): boolean;
    is(el:Html_dom_Element[], selector:string): boolean;

    jsSelect(selector:string, root?:string): Html_dom_Element[];
    jsSelect(selector:string, root?:Html_dom_Element): Html_dom_Element[];

    select(path:string, root?:Html_dom_Element, type?:string): Html_dom_Element[];

    selectNode(selector:string, root?:Html_dom_Element): Html_dom_Element;

    selectNumber(selector:string, root?:Html_dom_Element, defaultValue?:number): number;

    selectValue(selector:string, root?:Html_dom_Element, defaultValue?:string): string;

}


/*      Ext package      */


/**
 *      A set of useful static methods to deal with arrays; provide missing methods for older browsers.
 */
interface Ext_Array {

    clean(array:any[]): any[];

    clone(array:any[]): any[];

    contains(array:any[], item): boolean;

    difference(arrayA:any[], arrayB:any[]): any[];

    each(iterable:any /* Array/NodeList/Object */, fn:(item:any /*Object*/, index:number, all:any) => any /*boolean*/, scope?:Object,
         reverse?:boolean): boolean;

    erase(array:any[], index:number, removeCount:number): any[];

    every(array:any[], fn:Function, scope:Object): boolean;

    filter(array:any[], fn:Function, scope?): any[];

    flatten(array:any[]): any[];

    forEach(array:any[], fn:(item:Object, index:number, all:any[]) => boolean, scope?:Object): void;

    from(value:Object, newReference?:boolean): any[];

    include(array:any[], item:Object): void;

    indexOf(array:any[], item:Object, from?:number): number;

    insert(array:any[], index:number, items:any[]): any[];

    intersect(array1:any[], array2:any[], etc?:any[]): any[];

    map(array:any[], fn:Function, scope?:Object): any[];

    max(array:any[], comparisonFn?:(one:Object, two:Object) => number): Object;

    mean(array:any[]): number;

    merge(array1:any[], array2:any[], etc?:any[]): any[];

    min(array:any[], comparisonFn?:(one:Object, two:Object) => number): Object;

    pluck(array:any[], propertyName:string): any[];

    push(target:any[], ...elements:any[]): any[];

    remove(array:any[], item:Object): any[];

    replace(array:any[], index:number, removeCount:number, insert?:any[]): any[];

    slice(array:any[], begin:number, end:number): any[];

    some(array:any[], fn:Function, scope:Object): boolean;

    sort(array:any[], sortFn?:Function): any[];

    splice(array:any[], index:number, removeCount:number, ...elements:any[]): any[];

    sum(array:any[]): number;

    toArray(iterable:Object, start?:number, end?:number): any[];

    toMap(array:any[], getKey:string, scope?:Object): void;
    toMap(array:any[], getKey:(obj:Object) => string, scope?:Object): void;

    union(array1:any[], array2:any[], etc?:any[]): any[];

    unique(array:any[]): any[];

}


/**
 *      A collection of useful static methods to deal with strings.
 */
interface Ext_String {

    addCharacterEntities(entities:Object): void;

    capitalize(string:string): string;

    createVarName(s:string): string;

    ellipsis(value:string, length:number, word?:boolean): string;

    escape(string:string): string;

    escapeRegex(string:string): string;

    format(string:string, ...values:any[]): string;

    htmlDecode(value:string): string;

    htmlEncode(value:string): string;

    insert(s:string, value:string, index:number): string;

    leftPad(string:string, size:number, character?:string): string;

    repeat(pattern:string, count:number, sep:string): string;

    resetCharacterEntities(): void;

    splitWords(words): string[];

    toggle(string:string, value:string, other:string): string;

    trim(string:string): string;

    uncapitalize(string:string): string;

    urlAppend(url:string, string:string): string;

}


/**
 *      A collection of useful static methods to deal with function callbacks
 */
interface Ext_Function {

    alias(object:Object, methodName:string): Function;
    alias(object:Function, methodName:string): Function;

    bind(fn:Function, scope?:Object, args?:any[], appendArgs?:any /* boolean/Number */): Function;

    clone(method:Function): Function;

    createBuffered(fn:Function, buffer:number, scope?:Object, args?:any[]): Function;

    createDelayed(fn:Function, delay:number, scope?:Object, args?:any[], appendArgs?:any /* boolean/Number */): Function;

    createInterceptor(origFn:Function, newFn:Function, scope?:Object, returnValue?:Object): Function;

    createSequence(originalFn:Function, newFn:Function, scope?:Object): Function;

    createThrottled(fn:Function, interval:number, scope?:Object): Function;

    defer(fn:Function, millis:number, scope?:Object, args?:any[], appendArgs?:any /* boolean/Number */): number;

    flexSetter(setter:Function): Function;

    interceptAfter(object:Object, methodName:string, fn:Function, scope?:Object): Function;

    interceptBefore(object:Object, methodName:string, fn:Function, scope?:Object): Function;

    pass(fn:Function, args:any[], scope?:Object): Function;

}


/**
 *      A collection of useful static methods to deal with objects.
 */
interface Ext_Object {

    chain(object:Object): Object;

    each(object:Object, fn:Function, scope?:Object): void;

    fromQueryString(queryString:string, recursive?:boolean): Object;

    getKey(object:Object, value:any): string;

    getKeys(object:Object): string[];

    getSize(object:Object): number;

    getValues(object:Object): any[];

    merge(destination:Object, object:Object): Object;

    toQueryObjects(name:string, value:string, recursive?:boolean): Object[];
    toQueryObjects(name:string, value:string[], recursive?:boolean): Object[];

    toQueryString(object:Object, recursive?:boolean): string;

}


/**
 * Modified version of Douglas Crockford's JSON.js that doesn't mess with the Object prototype.
 */
interface Ext_JSON {

    decode(json:string, safe?:boolean): any;

    encode(object:any): string;

    encodeDate(date:Date): string;

    encodeString(string:string): string;

    encodeValue(object:any): string;

}


/**
 * Ext.Loader supports both asynchronous and synchronous loading approaches, and leverage their advantages for the best development flow.
 */
interface Ext_Loader {

    history: string[];

    addClassPathMappings(paths:Object): Ext_Loader;

    exclude(excludes:string): Object;
    exclude(excludes:string[]): Object;

    getConfig(name:string): Object;

    getPath(className:string): string;

    loadScript(options:{
        url:string;
        onLoad?:Function;
        onError?:Function;
        scope?:Object;
    }): void;

    onReady(fn:Function, scope:Object, withDomReady:boolean): void;

    require(expressions:string, fn?:Function, scope?:Object, excludes?:string): void;
    require(expressions:string, fn?:Function, scope?:Object, excludes?:string[]): void;
    require(expressions:string[], fn?:Function, scope?:Object, excludes?:string): void;
    require(expressions:string[], fn?:Function, scope?:Object, excludes?:string[]): void;

    setConfig(config:Object): Ext_Loader;

    setPath(name:Object): Ext_Loader;
    setPath(name:string, path?:string): Ext_Loader;

    syncRequire(expressions:string, fn?:Function, scope?:Object, excludes?:string): void;
    syncRequire(expressions:string, fn?:Function, scope?:Object, excludes?:string []): void;
    syncRequire(expressions:string[], fn?:Function, scope?:Object, excludes?:string): void;
    syncRequire(expressions:string[], fn?:Function, scope?:Object, excludes?:string []): void;

}


/**
 *      Represents an HTML fragment template. Templates may be precompiled for greater performance.
 */
interface Ext_Template extends Ext_Base {

    isTemplate: boolean;

    new(...html:string[]):Ext_Template;

    append(el:string, values:any /* Object/Array */, returnElement?:boolean): any;      // HTMLElement/Ext.Element
    append(el:Html_dom_Element, values:any /* Object/Array */, returnElement?:boolean): any;      // HTMLElement/Ext.Element
    append(el:Ext_dom_Element, values:any /* Object/Array */, returnElement?:boolean): any;      // HTMLElement/Ext.Element

    apply(values:Object): string;
    apply(values:any[]): string;

    applyOut(values:Object, out:any[]): any[];
    applyOut(values:any[], out:any[]): any[];

    applyTemplate(values:Object): string;
    applyTemplate(values:any[]): string;

    compile(): Ext_Template;

    insertAfter(el:string, values:any /* Object/Array */, returnElement?:boolean): any;    // HTMLElement/Ext.Element
    insertAfter(el:Html_dom_Element, values:any /* Object/Array */, returnElement?:boolean): any;    // HTMLElement/Ext.Element
    insertAfter(el:Ext_dom_Element, values:any /* Object/Array */, returnElement?:boolean): any;    // HTMLElement/Ext.Element

    insertBefore(el:string, values:any /* Object/Array */, returnElement?:boolean): any;     // HTMLElement/Ext.Element
    insertBefore(el:Html_dom_Element, values:any /* Object/Array */, returnElement?:boolean): any;     // HTMLElement/Ext.Element
    insertBefore(el:Ext_dom_Element, values:any /* Object/Array */, returnElement?:boolean): any;     // HTMLElement/Ext.Element

    insertFirst(el:string, values:any /* Object/Array */, returnElement?:boolean): any;     // HTMLElement/Ext.Element
    insertFirst(el:Html_dom_Element, values:any /* Object/Array */, returnElement?:boolean): any;     // HTMLElement/Ext.Element
    insertFirst(el:Ext_dom_Element, values:any /* Object/Array */, returnElement?:boolean): any;     // HTMLElement/Ext.Element

    overwrite(el:string, values:any /* Object/Array */, returnElement?:boolean): any;       // HTMLElement/Ext.Element
    overwrite(el:Html_dom_Element, values:any /* Object/Array */, returnElement?:boolean): any;       // HTMLElement/Ext.Element
    overwrite(el:Ext_dom_Element, values:any /* Object/Array */, returnElement?:boolean): any;       // HTMLElement/Ext.Element

    set(html:string, compile?:boolean): Ext_Template;
}


/**
 *      A template class that supports advanced functionality.
 */
interface Ext_XTemplate extends Ext_Template {

    new(...config:any[]): Ext_XTemplate;

    applyOut(values:Object, out:any[]): any[];
    applyOut(values:any[], out:any[]): any[];

    compile(): Ext_XTemplate;

}


interface Ext_AbstractComponent extends Ext_Base, Ext_state_Stateful, Ext_util_Animate,
    Ext_util_ElementContainer, Ext_util_Observable, Ext_util_Renderable {

    draggable: boolean;
    frameSize: Object;
    hasListeners: Object;
    isComponent: boolean;
    isObservable: boolean;
    maskOnDisable: boolean;
    ownerCt: Ext_container_Container;
    rendered: boolean;

    addCls(cls:string): Ext_Component;
    addCls(cls:string[]): Ext_Component;

    addClsWithUI(classes:string, skip:Object): void;
    addClsWithUI(classes:string[], skip:Object): void;

    addUIClsToElement(ui:string): void;

    disable(silent?:boolean) : void;

    doComponentLayout(): Ext_container_Container;

    enable(silent?:boolean): void;

    getBubbleTarget(): Ext_container_Container;

    getEl(): Ext_dom_Element;

    getHeight(): number;

    getId(): string;

    getItemId(): string;

    getLoader(): Ext_ComponentLoader;

    getPlugin(pluginId:string): Ext_AbstractPlugin;

    getSize(): Object;

    getSizeModel(ownerCtSizeModel:Object): Object;

    getWidth(): number;

    getXTypes(): String;

    hasCls(className:string): boolean;

    hasUICls(cls:string): boolean;

    is(selector:string): boolean;

    isDescendantOf(container:Ext_container_Container): boolean;

    isDisabled(): boolean;

    isDraggable(): boolean;

    isDroppable(): boolean;

    isFloating(): boolean;

    isHidden(): boolean;

    isLayoutSuspended(): boolean;

    isVisible(deep?:boolean): boolean;

    isXType(xtype:string, shallow?:boolean): boolean;

    nextNode(selector?:string): Ext_Component;

    nextSibling(selector?:string): Ext_Component;

    previousNode(selector?:string): Ext_Component;

    previousSibling(selector?:string): Ext_Component;

    registerFloatingItem(cmp:Object): void;

    removeCls(cls:string) : Ext_Component;
    removeCls(cls:string[]) : Ext_Component;

    removeClsWithUI(cls:string): void;
    removeClsWithUI(cls:string[]): void;

    removeUIClsFromElement(ui:string): void;

    renderTo(el:any): void;

    setBorder(border:string): void;
    setBorder(border:number): void;

    setDisabled(disabled:boolean);

    setDocked(dock:Object, layoutParent?:boolean): Ext_Component;

    setHeight(height:number) : Ext_Component;
    setHeight(height:string) : Ext_Component;

    setPosition(x:number, y?:number, animate?:any /* boolean/Object */) : Ext_Component;
    setPosition(x:number[], animate?:any /* boolean/Object */) : Ext_Component;
    setPosition(x:Object, animate?:any /* boolean/Object */) : Ext_Component;

    setSize(width:number, height:number) : Ext_Component;
    setSize(width:string, height:string) : Ext_Component;
    setSize(width:Object) : Ext_Component;

    setUI(ui:string): void;

    setVisible(visible:boolean) : Ext_Component;

    setWidth(width:number) : Ext_Component;
    setWidth(width:string) : Ext_Component;

    up(selector?:string): Ext_container_Container;

    update(htmlOrData:string, loadScripts?:boolean, callback?:Function): void;
    update(htmlOrData:Object, loadScripts?:boolean, callback?:Function): void;

    updateLayout(options?:{
        defer: boolean;
        isRoot: boolean;
    }): void;

}


/**
 *      Base class for all Ext components.
 *      The Component base class has built-in support for basic hide/show and enable/disable and size control behavior.
 */
interface Ext_Component extends Ext_AbstractComponent, Ext_util_Floating {

    floatParent: Ext_Component;
    zIndexManager: Ext_ZIndexManager;
    zIndexParent: Ext_container_Container;

    new(config?:string): Ext_Component;
    new(config?:Object): Ext_Component;
    new(config?:Html_dom_Element): Ext_Component;

    bubble(fn:Function, scope?:Object, args?:any[]): Ext_Component;

    cloneConfig(overrides:Object): Ext_Component;

    findParentBy(fn:Function): Ext_container_Container;

    findParentByType(xtype): Ext_container_Container;

    focus(selectText?:boolean, delay?:boolean): Ext_Component;
    focus(selectText?:boolean, delay?:number): Ext_Component;

    getBox(local?:boolean): Object;

    getPosition(local?:boolean): number[];

    getXType(): String;

    hide(animateTarget?:string, callback?:Function, scope?:Object): Ext_Component;
    hide(animateTarget?:Ext_dom_Element, callback?:Function, scope?:Object): Ext_Component;
    hide(animateTarget?:Ext_Component, callback?:Function, scope?:Object): Ext_Component;

    scrollBy(deltaX:number, deltaY:number, animate:any /* boolean/Object */): void;
    scrollBy(deltaX:number[], animate:any /* boolean/Object */): void;
    scrollBy(deltaX:Object, animate:any /* boolean/Object */): void;

    setAutoScroll(scroll:boolean): Ext_Component;

    setLoading(load:boolean, targetEl?:boolean): Ext_LoadMask;
    setLoading(load:Object, targetEl?:boolean): Ext_LoadMask;
    setLoading(load:String, targetEl?:boolean): Ext_LoadMask;

    setOverflowXY(overflowX:string, overflowY:string): Ext_Component;

    setPagePosition(x:number, y?:number, animate?:any /* boolean/Object */): Ext_Component;
    setPagePosition(x:number[], y?:number, animate?:any /* boolean/Object */): Ext_Component;

    show(animateTarget?:string, callback?:Function, scope?:Object): Ext_Component;
    show(animateTarget?:Ext_dom_Element, callback?:Function, scope?:Object): Ext_Component;

    showAt(x:number, y?:number, animate?:any /* boolean/Object */): Ext_Component;
    showAt(x:number[], animate?:any /* boolean/Object */): Ext_Component;
    showAt(x:Object, animate?:any /* boolean/Object */): Ext_Component;

    showBy(component:Ext_Component, position?:string, offsets?:number[]): Ext_Component;
    showBy(component:Ext_dom_Element, position?:string, offsets?:number[]): Ext_Component;

}


/**
 *      Simple helper class for easily creating image components. This renders an image tag to the DOM with the configured src.
 */
interface Ext_Img extends Ext_Component {

    new(config?:Object): Ext_Img;

    setSrc(src:string): void;

}


/**
 *      Provides searching of Components within Ext.ComponentManager (globally) or a specific
 *      Ext.container.Container on the document with a similar syntax to a CSS selector.
 */
interface Ext_ComponentQuery extends Ext_Base {

    is(component:Ext_Component, selector:string): boolean;

    query(selector:string, root?:Ext_container_Container) : Ext_Component[];

}


/**
 *      Just as Ext.Element wraps around a native DOM node,
 *      Ext.EventObject wraps the browser's native event-object normalizing cross-browser differences
 */
interface Ext_EventObject extends Ext_Base {

    A: number;
    ALT: number;
    B: number;
    BACKSPACE: number;
    C: number;
    CAPS_LOCK: number;
    CONTEXT_MENU: number;
    CTRL: number;
    D: number;
    DELETE: number;
    DOWN: number;
    E: number;
    EIGHT: number;
    END: number;
    ENTER: number;
    ESC: number;
    F: number;
    F1: number;
    F10: number;
    F11: number;
    F12: number;
    F2: number;
    F3: number;
    F4: number;
    F5: number;
    F6: number;
    F7: number;
    F8: number;
    F9: number;
    FIVE: number;
    FOUR: number;
    G: number;
    H: number;
    HOME: number;
    I: number;
    INSERT: number;
    J: number;
    K: number;
    L: number;
    LEFT: number;
    M: number;
    N: number;
    NINE: number;
    NUM_CENTER: number;
    NUM_DIVISION: number;
    NUM_EIGHT: number;
    NUM_FIVE: number;
    NUM_FOUR: number;
    NUM_MINUS: number;
    NUM_MULTIPLY: number;
    NUM_NINE: number;
    NUM_ONE: number;
    NUM_PERIOD: number;
    NUM_PLUS: number;
    NUM_SEVEN: number;
    NUM_SIX: number;
    NUM_THREE: number;
    NUM_TWO: number;
    NUM_ZERO: number;
    O: number;
    ONE: number;
    P: number;
    PAGE_DOWN: number;
    PAGE_UP: number;
    PAUSE: number;
    PRINT_SCREEN: number;
    Q: number;
    R: number;
    RETURN: number;
    RIGHT: number;
    S: number;
    SEVEN: number;
    SHIFT: number;
    SIX: number;
    SPACE: number;
    T: number;
    TAB: number;
    THREE: number;
    TWO: number;
    U: number;
    UP: number;
    V: number;
    W: number;
    WHEEL_SCALE: number;
    X: number;
    Y: number;
    Z: number;
    ZERO: number;
    altKey: boolean;
    ctrlKey: boolean;
    shiftKey: boolean;

    correctWheelDelta(delta:number): void;

    getCharCode(): number;

    getKey(): number;

    getPoint(): Ext_util_Point;

    getRelatedTarget(selector?:string, maxDepth?:number, returnEl?:boolean): any;   // HTMLElement/Ext.dom.Element
    getRelatedTarget(selector?:string, maxDepth?:Html_dom_Element, returnEl?:boolean): any;   // HTMLElement/Ext.dom.Element

    getTarget(selector?:string, maxDepth?:number, returnEl?:boolean): any;   // HTMLElement/Ext.dom.Element
    getTarget(selector?:string, maxDepth?:Html_dom_Element, returnEl?:boolean): any;   // HTMLElement/Ext.dom.Element

    getWheelDelta(): number;

    getWheelDeltas(): Object;

    getX(): number;

    getXY(): number[];

    getY(): number;

    hasModifier(): boolean;

    injectEvent(target?:Html_dom_Element): void;
    injectEvent(target?:Ext_dom_Element): void;

    isNavKeyPress(): boolean;

    isSpecialKey(): boolean;

    preventDefault(): void;

    stopEvent(): void;

    stopPropagation(): void;

    within(el:string, related?:boolean, allowEl?:boolean): boolean;
    within(el:Html_dom_Element, related?:boolean, allowEl?:boolean): boolean;
    within(el:Ext_dom_Element, related?:boolean, allowEl?:boolean): boolean;

}


/**
 *      The AbstractPlugin class is the base class from which user-implemented plugins should inherit.
 */
interface Ext_AbstractPlugin extends Ext_Base {

    destroy(): void;

    disable(): void;

    enable(): void;

    init(client:Ext_Component): void;

}


/**
 *      A modal, floating Component which may be shown above a specified Component while loading data.
 */
interface Ext_LoadMask extends Ext_Component {

    new(component:Ext_Component, config?:Object): Ext_LoadMask;

    bindStore(store:Ext_data_Store): void;

    hide(animateTarget?:string, callback?:Function, scope?:Object): Ext_Component;
    hide(animateTarget?:Ext_dom_Element, callback?:Function, scope?:Object): Ext_Component;
    hide(animateTarget?:Ext_Component, callback?:Function, scope?:Object): Ext_Component;

    show(animateTarget?:string, callback?:Function, scope?:Object): Ext_Component;
    show(animateTarget?:Ext_dom_Element, callback?:Function, scope?:Object): Ext_Component;
    show(animateTarget?:Ext_Component, callback?:Function, scope?:Object): Ext_Component;

}


/*      Menu package        */


/**
 *      A menu object. This is the container to which you may add menu items.
 */
interface Ext_menu_Menu extends Ext_panel_Panel {

    isMenu: boolean;

    parentMenu: Ext_menu_Menu;


    new(config?:Object): Ext_menu_Menu;


    canActivateItem(item:Object): boolean;

    deactivateActiveItem(andBlurFocusedItem:Object): void;

    getBubbleTarget(): Ext_container_Container;

    hide(animateTarget?:string, callback?:Function, scope?:Object): Ext_Component;
    hide(animateTarget?:Ext_dom_Element, callback?:Function, scope?:Object): Ext_Component;
    hide(animateTarget?:Ext_Component, callback?:Function, scope?:Object): Ext_Component;

    show(animateTarget?:string, callback?:Function, scope?:Object): Ext_Component;
    show(animateTarget?:Ext_dom_Element, callback?:Function, scope?:Object): Ext_Component;
    show(animateTarget?:Ext_Component, callback?:Function, scope?:Object): Ext_Component;

    showBy(component:Ext_dom_Element, position?:string, offsets?:number[]): Ext_Component;
    showBy(component:Ext_Component, position?:string, offsets?:number[]): Ext_Component;

}


/**
 *      A base class for all menu items that require menu-related functionality such as click handling, sub-menus, icons, etc.
 */
interface Ext_menu_Item extends Ext_Component {

    activated: boolean;

    maskOnDisable: boolean;

    menu: Ext_menu_Menu;

    parentMenu: Ext_menu_Menu;


    setHandler(fn:Function, scope?:Object): void;

    setIcon(icon:string): void;

    setIconCls(iconCls:string): void;

    setMenu(menu:Ext_menu_Menu, destroyMenu?:boolean): void;
    setMenu(menu:Object, destroyMenu?:boolean): void;

    setText(text:string): void;

    setTooltip(tooltip:string): Ext_menu_Item;
    setTooltip(tooltip:Object): Ext_menu_Item;

}


interface Ext_menu_CheckItem extends Ext_menu_Item {

    disableCheckChange(): void;

    enableCheckChange(): void;

    setChecked(checked:boolean, suppressEvents?:boolean): void;

}


/*      Button package      */


interface Ext_button_Button extends Ext_Component {

    disabled: boolean;

    hidden: boolean;

    menu: Ext_menu_Menu;

    pressed: boolean;

    template: Ext_Template;

    new(config?:Object): Ext_button_Button;

    disable(silent?:boolean): void;

    enable(silent?:boolean): void;

    getTemplateArgs(): Object;

    getText() : string;

    hasVisibleMenu(): boolean;

    hideMenu(): Ext_button_Button;

    setHandler(handler:Function, scope?:Object): Ext_button_Button;

    setIcon(icon:string): Ext_button_Button;

    setIconCls(cls:string): Ext_button_Button;

    setParams(params:Object): void;

    setScale(scale:string): void;

    setText(text:string): Ext_button_Button;

    setTextAlign(align:string): void;

    setTooltip(tooltip:string): Ext_button_Button;
    setTooltip(tooltip:Object): Ext_button_Button;

    setUI(ui:string): void;

    showMenu(fromEvent:Object): void;

    toggle(state?:boolean, suppressEvent?:boolean): Ext_button_Button;

}


interface Ext_button_Split extends Ext_button_Button {

    setArrowHandler(handler:Function, scope:Object): void;

}


interface Ext_button_Cycle extends Ext_button_Split {

    menu: Ext_menu_Menu;

    getActiveItem(): Ext_menu_CheckItem;

    setActiveItem(item:Ext_menu_CheckItem, suppressEvent?:boolean): void;

    toggleSelected(): void;

}


/*      Container package       */


/**
 *      An abstract base class which provides shared methods for Containers across the Sencha product line.
 */
interface Ext_container_AbstractContainer extends Ext_Component {

    items: Ext_util_AbstractMixedCollection;

    add(component:Ext_Component[]): Ext_Component[];
    add(...component:Ext_Component[]): Ext_Component[];
    add(component:Object[]): Ext_Component[];
    add(...component:Object[]): Ext_Component[];

    cascade(fn:Function, scope?:Object, args?:any[]): Ext_container_Container;

    child(selector?:string): Ext_Component;

    disable(silent?:boolean): Ext_container_AbstractContainer;

    doLayout(): Ext_container_Container;

    down(selector?:string): Ext_Component;

    getComponent(comp:string): Ext_Component;
    getComponent(comp:number): Ext_Component;

    getLayout(): Ext_layout_container_Container;

    insert(index:number, component:Ext_Component): Ext_Component;
    insert(index:number, component:Object): Ext_Component;

    isAncestor(possibleDescendant:Ext_Component): boolean;

    move(fromIdx:number, toIdx:number): Ext_Component;

    query(selector?:string): Ext_Component[];

    queryBy(fn:Function, scope?:Object): Ext_Component[];

    queryById(id:string): Ext_Component;

    remove(component:Ext_Component, autoDestroy?:boolean): Ext_Component;
    remove(component:string, autoDestroy?:boolean): Ext_Component;

    removeAll(autoDestroy?:boolean): Ext_Component[];

}


/**
 *      Base class for any Ext.Component that may contain other Components.
 */
interface Ext_container_Container extends Ext_container_AbstractContainer {

    new(config?:Object): Ext_container_Container;

    getChildByElement(el:Ext_dom_Element, deep:boolean): Ext_Component;
    getChildByElement(el:Html_dom_Element, deep:boolean): Ext_Component;
    getChildByElement(el:string, deep:boolean): Ext_Component;

}


interface Ext_container_DockingContainer extends Ext_Base {

    addDocked(component:Object, pos?:number): Ext_Component;
    addDocked(component:Object[], pos?:number): Ext_Component[];
    addDocked(component:Ext_Component, pos?:number): Ext_Component;
    addDocked(component:Ext_Component[], pos?:number): Ext_Component[];

    getDockedComponent(comp:string): Ext_Component;
    getDockedComponent(comp:number): Ext_Component;

    getDockedItems(selector:string, beforeBody:boolean): Ext_Component[];

    insertDocked(pos:number, component:Object): void;
    insertDocked(pos:number, component:Object[]): void;

    removeDocked(item:Ext_Component, autoDestroy?:boolean): void;
}


/**
 *      A specialized container representing the viewable application area (the browser viewport).
 */
interface Ext_container_Viewport extends Ext_container_Container {

    isViewport: boolean;


    new(config?:Object): Ext_container_Viewport;

}


/**
 *      Provides a container for arranging a group of related Buttons in a tabular manner.
 */
interface Ext_container_ButtonGroup extends Ext_panel_Panel {

    // nothing here

}


/*      Components      */


interface Ext_view_AbstractView extends Ext_Component {

    bindStore(store): void;

    collectData(records:Ext_data_Model[], startIndex:number): Object[];

    deselect(records:Ext_data_Model[], suppressEvent:boolean): void;
    deselect(records:number, suppressEvent:boolean): void;

    findItemByChild(node:Html_dom_Element): Html_dom_Element;

    findTargetByEvent(e:Ext_EventObject): void;

    getNode(nodeInfo:Html_dom_Element): Html_dom_Element;
    getNode(nodeInfo:string): Html_dom_Element;
    getNode(nodeInfo:number): Html_dom_Element;
    getNode(nodeInfo:Ext_data_Model): Html_dom_Element;

    getNodes(start?:number, end?:number): Html_dom_Element[];

    getRecord(node): Ext_data_Model;

    getRecords(nodes): Ext_data_Model[];

    getSelectedNodes(): Html_dom_Element[];

    getSelectionModel(): Ext_selection_Model;

    getStore(): Ext_data_Store;

    indexOf(nodeInfo:Html_dom_Element): number;
    indexOf(nodeInfo:string): number;
    indexOf(nodeInfo:number): number;
    indexOf(nodeInfo:Ext_data_Model): number;

    isSelected(node:Html_dom_Element): boolean;
    isSelected(node:number): boolean;
    isSelected(node:Ext_data_Model): boolean;

    prepareData(data:Object, recordIndex:number, record:Ext_data_Model): any;    // Array/Object
    prepareData(data:Object[], recordIndex:number, record:Ext_data_Model): any;    // Array/Object

    refresh(): void;

    refreshNode(index:number): void;

}


/**
 *      A mechanism for displaying data using custom layout templates and formatting.
 */
interface Ext_view_View extends Ext_view_AbstractView {

    clearHighlight(): void;

    focusNode(rec:Ext_data_Model): void;

    highlightItem(item:Html_dom_Element): void;

    refresh(): void;

}


/**
 *      This class encapsulates the user interface for a tabular data set.
 *      It acts as a centralized manager for controlling the various interface elements of the view.
 */
interface Ext_view_Table extends Ext_view_View {

    new(config?:Object): Ext_view_Table;

    addRowCls(rowInfo:Html_dom_Element, cls:string): void;
    addRowCls(rowInfo:string, cls:string): void;
    addRowCls(rowInfo:number, cls:string): void;
    addRowCls(rowInfo:Ext_data_Model, cls:string): void;

    collectData(records:Ext_data_Model[], startIndex:number): Object[];

    focusRow(rowIdx:Html_dom_Element): void;
    focusRow(rowIdx:string): void;
    focusRow(rowIdx:number): void;
    focusRow(rowIdx:Ext_data_Model): void;

    getFeature(id:string): Ext_grid_feature_Feature;

    getPosition(local?:boolean): number[];

    getRowClass(record:Ext_data_Model, index:number, rowParams:Object, store:Ext_data_Store): string;

    getTableChunker(): void;

    refresh(): void;

    removeRowCls(rowInfo:Html_dom_Element, cls:string): void;
    removeRowCls(rowInfo:string, cls:string): void;
    removeRowCls(rowInfo:number, cls:string): void;
    removeRowCls(rowInfo:Ext_data_Model, cls:string): void;

}


/**
 *      The grid View class provides extra Ext.grid.Panel specific functionality to the Ext.view.Table.
 */
interface Ext_grid_View extends Ext_view_Table {

    // nothing here

}


/**
 *      Used as a view by TreePanel.
 */
interface Ext_tree_View extends Ext_view_Table {

    collapse(record:Ext_data_Model, deep?:boolean, callback?:Function, scope?:Object): void;

    collectData(records:Ext_data_Model[], startIndex:number): Object[];

    expand(record:Ext_data_Model, deep?:boolean, callback?:Function, scope?:Object): void;

    getTreeStore(): Ext_data_TreeStore;

    toggle(record:Ext_data_Model, deep?:boolean, callback?:Function, scope?:Object): void;

}


/**
 *      A base class which provides methods common to Panel classes across the Sencha product range.
 */
interface Ext_panel_AbstractPanel extends Ext_container_Container, Ext_container_DockingContainer {

    body : Ext_dom_Element;

    isPanel: boolean;

    addBodyCls(cls:string): Ext_panel_Panel;

    addUIClsToElement(ui:string): void;

    //getComponent(comp:string): Ext_Component;

    removeBodyCls(cls:string): Ext_panel_Panel;

    removeUIClsFromElement(ui:string): void;

    setBodyStyle(style:string, value?:string): Ext_panel_Panel;
    setBodyStyle(style:Object): Ext_panel_Panel;

}


/**
 *      Panel is a container that has specific functionality and structural components
 *      that make it the perfect building block for application-oriented user interfaces.
 */
interface Ext_panel_Panel extends Ext_panel_AbstractPanel {

    dd : Ext_dd_DragSource;

    new(config?:{
        animCollapse?: boolean;
        bbar?: any;      // Object/Object[]
        buttonAlign?: string;
        buttons?: any;   // Object/Object[]
        closable?: boolean;
        closeAction?: string;
        collapseDirection?: string;
        collapseFirst?: boolean;
        collapseMode?: string;
        collapsed?: boolean;
        collapsedCls?: string;
        collapsible?: boolean;
        dockedItems?: any;      // Object/Object[]
        fbar?: any;         // Object/Object[]
        floatable?: boolean;
        frame?: boolean;
        frameHeader?: boolean;
        header?: any;       // boolean/Object
        headerPosition?: string;
        hideCollapseTool?: boolean;
        icon?: string;
        iconCls?: string;
        lbar?: any;         // Object/Object[]
        manageHeight?: boolean;
        minButtonWidth?: number;
        overlapHeader?: boolean;
        placeholder?: any;      // Ext.Component/Object
        placeholderCollapseHideMode?: number;
        rbar?: any;     // Object/Object[]
        tbar?: any;     // Object/Object[]
        title?: string;
        titleAlign?: string;
        titleCollapse?: boolean;
        tools?: any[];      //Object[]/Ext.panel.Tool[]
    }): Ext_panel_Panel;

    addTool(tool:Object[]): void;
    addTool(tool:Ext_panel_Tool[]): void;

    close(): void;

    collapse(direction?:string, animate?:boolean): Ext_panel_Panel;
    collapse(direction?:string, animate?:number): Ext_panel_Panel;

    expand(animate?:boolean): Ext_panel_Panel;

    getCollapsed(): any;      // boolean/String;

    getHeader(): Ext_panel_Header;

    getState(): Object;

    isVisible(deep?:boolean): boolean;

    setBorder(border:string): void;
    setBorder(border:number): void;

    setIcon(newIcon:string): void;

    setIconCls(newIconCls:string): void;

    setTitle(newTitle:string): void;

    setUI(ui:string): void;

    toggleCollapse() : Ext_panel_Panel;

}


/**
 *      Simple header class which is used for on Ext.panel.Panel and Ext.window.Window.
 */
interface Ext_panel_Header extends Ext_container_Container {

    isHeader: boolean;

    addTool(tool:Object): void;
    addTool(tool:Ext_panel_Tool): void;

    addUIClsToElement(ui:string): void;

    getTools() : Ext_panel_Tool[];

    removeUIClsFromElement(ui:string): void;

    setIcon(icon:string): void;

    setIconCls(cls:string): void;

    setTitle(title:string): void;

}


/**
 *      This class is used to display small visual icons in the header of a panel.
 *      There are a set of 25 icons that can be specified by using the type config.
 */
interface Ext_panel_Tool extends Ext_Component {

    setType(type:string) : Ext_panel_Tool;

}


/**
 *      A specialized panel intended for use as an application window.
 *      Windows are floated, resizable, and draggable by default.
 */
interface Ext_window_Window extends Ext_panel_Panel {

    dd: Ext_dd_DragSource;      //TODO: Ext_util_ComponentDragger throws exception

    isWindow: boolean;

    new(config?:Object): Ext_window_Window;

    applyState(state:Object): void;

    getDefaultFocus(): void;

    maximize(): Ext_window_Window;

    minimize(): Ext_window_Window;

    restore(): Ext_window_Window;

    toggleMaximize(): Ext_window_Window;

}


/**
 *      Utility class for generating different styles of message boxes.
 *      The singleton instance, Ext.MessageBox alias Ext.Msg can also be used.
 */
interface Ext_window_MessageBox extends Ext_window_Window {

    CANCEL: number;

    ERROR: string;

    INFO: string;

    NO: number;

    OK: number;

    OKCANCEL: number;

    QUESTION: string;

    WARNING: string;

    YES: number;

    YESNO: number;

    YESNOCANCEL: number;

    buttonText: Object;

    defaultTextHeight: number;

    minProgressWidth: number;

    minPromptWidth: number;

    alert(title:string, msg:string, fn?:(buttonId:string, text:string, opts:Object) => void, scope?:Object): Ext_window_MessageBox;

    confirm(title:string, msg:string, fn?:(buttonId:string, text:string, opts:Object) => void, scope?:Object): Ext_window_MessageBox;

    //hide(animateTarget:string, callback?:Function, scope?:Object): Ext_Component;

    progress(title:string, msg:string, progressText?:string): Ext_window_MessageBox;

    prompt(title:string, msg:string, fn?:(buttonId:string, text:string, opts:Object) => void, scope?:Object, multiline?:boolean,
           value?:string): Ext_window_MessageBox;

    setIcon(icon:string): Ext_window_MessageBox;

    /*    show(config:{
     animateTarget?: string;
     buttons?: number;
     closable?: boolean;
     cls?: string;
     defaultTextHeight?: number;
     fn?: (buttonId:string, text:string, opts:Object) => void;
     buttonText?: Object;
     scope?:Object;
     icon?:string;
     iconCls?: string;
     maxWidth?; number;
     minWidth?: number;
     modal?: boolean;
     msg?: string;
     multiline?: boolean;
     progress?: boolean;
     progressText?: string;
     prompt?: boolean;
     proxyDrag?: boolean;
     title?: string;
     value?: string;
     wait?: boolean;
     waitConfig?: Object;
     width?: number;
     }): Ext_window_MessageBox;*/

    updateProgress(value?:number, progressText?:string, msg?:string): Ext_window_MessageBox;

    wait(msg:string, title?:string, config?:Object): Ext_window_MessageBox;

}


/**
 *      TablePanel is the basis of both TreePanel and GridPanel.
 */
interface Ext_panel_Table extends Ext_panel_Panel {

    hasView: boolean;

    optimizedColumnMove: boolean;

    applyState(state:Object): void;

    getSelectionModel(): Ext_selection_Model;

    getState(): Object;

    getStore(): Ext_data_Store;

    getView(): Ext_view_Table;

}


/**
 *      Grids are an excellent way of showing large amounts of tabular data on the client side.
 */
interface Ext_grid_Panel extends Ext_panel_Table {

    new(config?:Object): Ext_grid_Panel;

    reconfigure(store?:Ext_data_Store, columns?:Object[]): void;

}


/**
 *      A feature is a type of plugin that is specific to the Ext.grid.Panel.
 */
interface Ext_grid_feature_Feature extends Ext_util_Observable {

    collectData : boolean;

    disabled: boolean;

    eventPrefix: string;

    eventSelector: string;

    grid: Ext_grid_Panel;

    hasFeatureEvent: boolean;

    view: Ext_view_Table;

    new(config?:Object): Ext_grid_feature_Feature;

    attachEvents(): void;

    disable(): void;

    enable(): void;

    getAdditionalData(data:Object, idx:number, record:Ext_data_Model, orig:Object): void;

    getFireEventArgs(eventName:string, view:Ext_view_Table, featureTarget:Object, e:Ext_EventObject): void;

    getMetaRowTplFragments(): void;

    mutateMetaRowTpl(metaRowTplArray:string[]): void;

}


/**
 *      The TreePanel provides tree-structured UI representation of tree-structured data.
 */
interface Ext_tree_Panel extends Ext_panel_Table {

    new(config?:Object): Ext_tree_Panel;

    collapseAll(callback?:Function, scope?:Object): void;

    collapseNode(record:Ext_data_Model, deep?:boolean, callback?:Function, scope?:Object): void;

    expandAll(callback?:Function, scope?:Object): void;

    expandNode(record:Ext_data_Model, deep?:boolean, callback?:Function, scope?:Object): void;

    expandPath(path:string, field?:string, separator?:string, callback?:Function, scope?:Object): void;

    getChecked(): Ext_data_NodeInterface[];

    getRootNode(): Ext_data_NodeInterface;

    selectPath(path:string, field?:string, separator?:string, callback?:Function, scope?:Object): void;

    setRootNode(root): Ext_data_NodeInterface;

}


/**
 *      A basic tab container. TabPanels can be used exactly like a standard Ext.panel.Panel for layout purposes,
 *      but also have special support for containing child Components (items) that are managed using a CardLayout layout manager,
 *      and displayed as separate tabs.
 */
interface Ext_tab_Panel extends Ext_panel_Panel {

    tabBar: Ext_tab_Bar;

    getActiveTab(): Ext_Component;

    getTabBar(): Ext_tab_Bar;

    setActiveTab(card:string): Ext_Component;
    setActiveTab(card:number): Ext_Component;
    setActiveTab(card:Ext_Component): Ext_Component;

}


/**
 *      TabBar is used internally by a TabPanel and typically should not need to be created manually.
 *      The tab bar automatically removes the default title provided by Ext.panel.Header
 */
interface Ext_tab_Bar extends Ext_panel_Header {

    isTabBar: boolean;

    getLayout() : Ext_layout_container_Container;

}


/**
 *      Tracks what records are currently selected in a databound component.
 */
interface Ext_selection_Model extends Ext_util_Observable {

    selected : Ext_util_MixedCollection;

    new(config?:Object): Ext_selection_Model;

    bindStore(store?:string, initial?:boolean): void;
    bindStore(store?:Ext_data_AbstractStore, initial?:boolean): void;

    deselect(records:Ext_data_Model[], suppressEvent?:boolean): void;
    deselect(records:number, suppressEvent?:boolean): void;

    deselectAll(suppressEvent?:boolean): void;

    getCount(): number;

    getLastSelected(): Ext_data_Model;

    getSelection(): Ext_data_Model[];

    getSelectionMode(): string;

    hasSelection(): boolean;

    isFocused(record:Ext_data_Model): void;

    isLocked(): boolean;

    isSelected(record:Ext_data_Model): boolean;

    select(records:Ext_data_Model[], keepExisting?:boolean, suppressEvent?:boolean): void;
    select(records:number, keepExisting?:boolean, suppressEvent?:boolean): void;

    selectAll(suppressEvent?:boolean): void;

    selectRange(startRow:Ext_data_Model, endRow:Ext_data_Model, keepExisting?:boolean): void;
    selectRange(startRow:number, endRow:number, keepExisting?:boolean): void;

    setLastFocused(record:Ext_data_Model): void;

    setLocked(locked:boolean): void;

    setSelectionMode(selMode:string): void;

}

interface Ext_selection_RowModel extends Ext_selection_Model {

}


/*      Toolbar package     */


/**
 *      The base class that other non-interacting Toolbar Item classes should extend
 *      in order to get some basic common toolbar item functionality.
 */
interface Ext_toolbar_Item extends Ext_Component {

    disable(silent?:boolean): void;

    enable(silent?:boolean): void;

    focus(selectText?:boolean, delay?:number): Ext_Component;

}


/**
 *      Basic Toolbar class. Although the defaultType for Toolbar is button,
 *      Toolbar elements (child items for the Toolbar container) may be virtually any type of Component.
 */
interface Ext_toolbar_Toolbar extends Ext_container_Container {

    isToolbar: boolean;

    new(config?:Object): Ext_toolbar_Toolbar;

    //add(...args:Ext_Component[]) : any;      // Ext.Component[]/Ext.Component
    //add(...args:Object[]) : any;      // Ext.Component[]/Ext.Component
    //add(...args:string[]) : any;      // Ext.Component[]/Ext.Component
    //add(...args:Html_dom_Element[]) : any;      // Ext.Component[]/Ext.Component
    //add(...args:Ext_toolbar_Item[]) : any;      // Ext.Component[]/Ext.Component

    insert(index:number, component:Ext_Component): Ext_Component;
    insert(index:number, component:Object): Ext_Component;
    insert(index:number, component:string): Ext_Component;
    insert(index:number, component:Html_dom_Element): Ext_Component;

}


/**
 *      A non-rendering placeholder item which instructs the Toolbar's Layout to begin using the right-justified button container.
 */
interface Ext_toolbar_Fill extends Ext_Component {

    isFill: boolean;

}


/*      Form package        */


/**
 *      This mixin provides a common interface for the logical behavior and state of form fields
 */
interface Ext_form_field_Field extends Ext_Base {

    isFormField: boolean;

    originalValue: Object;

    batchChanges(fn:Function): void;

    checkChange(): void;

    checkDirty(): void;

    clearInvalid(): void;

    extractFileInput(): Html_dom_Element;

    getErrors(value:Object): string[];

    getModelData(): Object;

    getName(): string;

    getSubmitData(): Object;

    getValue(): Object;

    initField(): void;

    initValue(): void;

    isDirty(): boolean;

    isEqual(value1:Object, value2:Object): boolean;

    isFileUpload(): boolean;

    isValid(): boolean;

    markInvalid(errors:string): void;
    markInvalid(errors:string[]): void;

    reset(): void;

    resetOriginalValue(): void;

    setValue(value) : Ext_form_field_Field;

    validate(): boolean;

}


interface Ext_form_field_Text extends Ext_form_field_Base {


    new(config?:Object): Ext_form_field_Text;


    applyState(state:Object): void;

    autoSize(): void;

    getErrors(value:Object): string[];

    getRawValue(): string;

    getState(): Object;

    getSubTplData(): Object;

    processRawValue(value:string): string;

    reset(): void;

    selectText(start?:number, end?:number): void;

    setValue(value:Object): Ext_form_field_Text;

}


/**
 *      A display-only text field which is not validated and not submitted.
 */
interface Ext_form_field_Display extends Ext_form_field_Base {

    new(config?:Object): Ext_form_field_Display;


    getRawValue(): string;

    getSubTplData(): Object;

    isDirty(): boolean;

    isValid(): boolean;

    setRawValue(value:Object): Object;

    validate(): boolean;

}


/**
 *      Single checkbox field. Can be used as a direct replacement for traditional checkbox fields.
 *      Also serves as a parent class for radio buttons.
 */
interface Ext_form_field_Checkbox extends Ext_form_field_Base {

    boxLabelEl: Ext_dom_Element;

    originalValue: Object;


    new(config?:Object): Ext_form_field_Checkbox;


    //getRawValue() : boolean;

    getSubTplData() : Object;

    getSubmitValue() : string;

    getValue() : boolean;

    initValue(): void;

    resetOriginalValue(): void;

    setRawValue(value): boolean;

    setReadOnly(readOnly:boolean): void;

    setValue(checked:boolean): Ext_form_field_Checkbox;

    valueToRaw(value:Object): Object;

}


/**
 *      Provides a convenient wrapper for TextFields that adds a clickable trigger button (looks like a combobox by default)
 */
interface Ext_form_field_Trigger extends Ext_form_field_Text {

    inputCell: Ext_dom_Element;

    triggerEl: Ext_dom_CompositeElement;

    triggerWrap: Ext_dom_Element;


    new(config?:Object): Ext_form_field_Trigger;


    getSubTplData(): Object;

    getSubTplMarkup(): string;

    getTriggerWidth(): number;

    setEditable(editable:boolean): void;

    setReadOnly(readOnly:boolean): void;

}


/**
 *      An abstract class for fields that have a single trigger which opens a "picker" popup below the field,
 *      e.g. a combobox menu list or a date picker.
 */
interface Ext_form_field_Picker extends Ext_form_field_Trigger {

    isExpanded: boolean;


    new(config?:Object): Ext_form_field_Picker;


    collapse(): void;

    createPicker(): void;

    expand(): void;

    getPicker() : Ext_Component;

}


/**
 *      A combobox control with support for autocomplete, remote loading, and many other features.
 */
interface Ext_form_field_ComboBox extends Ext_form_field_Picker {

    lastQuery: string;


    new(config?:Object): Ext_form_field_ComboBox;


    clearValue(): void;

    createPicker(): void;

    doQuery(queryString:string, forceAll?:boolean, rawQuery?:boolean): boolean;

    findRecord(field:string, value:Object): Ext_data_Model;

    findRecordByDisplay(value:Object): Ext_data_Model;

    findRecordByValue(value:Object): Ext_data_Model;

    getStore(): Ext_data_Store;

    getSubTplData(): Object;

    getSubmitValue(): string;

    getValue(): Object;

    select(r:Object): void;
    select(r:Ext_data_Model): void;

    //setValue(value:string) : Ext_form_field_Field;
    //setValue(value:string[]) : Ext_form_field_Field;

}


/**
 *      This is a singleton object which contains a set of commonly used field validation functions
 *      and provides a mechanism for creating reusable custom field validations.
 */
interface Ext_form_field_VTypes extends Ext_Base {

    alphaMask: RegExp;

    alphaText: string;

    alphanumMask: RegExp;

    alphanumText: string;

    emailMask: RegExp;

    emailText: string;

    urlText: string;

    alpha(value:Object): boolean;

    alphanum(value:Object): boolean;

    email(value:Object): boolean;

    url(value:Object): boolean;

}


/**
 *      A mixin which allows a component to be configured and decorated with a label and/or error message as is common for form fields.
 */
interface Ext_form_Labelable extends Ext_Base {

    bodyEl: Ext_dom_Element;

    errorEl: Ext_dom_Element;

    isFieldLabelable: boolean;

    labelCell: Ext_dom_Element;

    labelEl: Ext_dom_Element;

    getActiveError(): string;

    getActiveErrors(): string[];

    getFieldLabel(): string;

    getInputId(): string;

    getLabelWidth(): Number;

    hasActiveError(): boolean;

    hasVisibleLabel(): boolean;

    initLabelable(): void;

    setActiveError(msg:string): void;

    setActiveErrors(errors:string[]): void;

    setFieldDefaults(defaults:Object): void;

    setFieldLabel(label:string): void;

    trimLabelSeparator(): string;

    unsetActiveError(): void;

}


/**
 *      Base class for form fields that provides default event handling,
 *      rendering, and other common functionality needed by all form field types.
 */
interface Ext_form_field_Base extends Ext_Component, Ext_form_field_Field, Ext_form_Labelable {

    inputEl : Ext_dom_Element;

    maskOnDisable: boolean;

    clearInvalid(): void;

    doComponentLayout(): Ext_container_Container;

    extractFileInput(): Html_dom_Element;

    getInputId(): string;

    getRawValue(): string;

    getSubTplData(): Object;

    getSubTplMarkup(): any;

    getSubmitData(): Object;

    getSubmitValue(): string;

    getValue(): Object;

    isFileUpload(): boolean;

    isValid(): boolean;

    markInvalid(errors:string): void;
    markInvalid(errors:string[]): void;

    processRawValue(value:Object): Object;

    rawToValue(rawValue): Object;

    setFieldStyle(style:string): void;
    setFieldStyle(style:Object): void;
    setFieldStyle(style:Function): void;

    setRawValue(value): Object;

    setReadOnly(readOnly:boolean): void;

    setValue(value) : Ext_form_field_Field;

    validateValue(value:Object) : boolean;

    valueToRaw(value:Object) : Object;

}


/**
 *      A mixin for Ext.container.Container components that are likely to have form fields in their items subtree.
 */
interface Ext_form_FieldAncestor extends Ext_Base {

    // nothing here

}


/**
 *       Panel is a container that has specific functionality and structural components
 *       that make it the perfect building block for application-oriented user interfaces.
 */
interface Ext_form_Panel extends Ext_panel_AbstractPanel, Ext_form_FieldAncestor {

    checkChange(): void;

    getForm(): Ext_form_Basic;

    getRecord(): Ext_data_Model;

    getValues(asString?:boolean, dirtyOnly?:boolean, includeEmptyText?:boolean, useDataValues?:boolean): any;     // String/Object2

    hasInvalidField(): void;

    isDirty(): boolean;

    isValid(): boolean;

    load(options:Object): void;

    loadRecord(record:Ext_data_Model): Ext_form_Basic;

    startPolling(interval:number): void;

    stopPolling(): void;

    submit(options:Object): void;

}


/**
 *      The subclasses of this class provide actions to perform upon Forms.
 */
interface Ext_form_action_Action extends Ext_Base {

    failureType: string;

    response: Object;

    result: Object;

    type: string;

    CLIENT_INVALID: string;

    CONNECT_FAILURE: string;

    LOAD_FAILURE: string;

    SERVER_INVALID: string;

    new(config?:Object): Ext_form_action_Action;

    run(): void;

}


interface Ext_form_Basic_ActionOptions {
    url?: string;
    method?: string;
    params?: any;       // string/Object
    headers?: Object;
    success?: (form:Ext_form_Basic, action:Ext_form_action_Action) => void;
    failure?: (form:Ext_form_Basic, action:Ext_form_action_Action) => void;
    scope?: Object;
    clientValidation?: boolean;
}


/**
 *      Provides input field management, validation, submission, and form loading services
 *      for the collection of Field instances within a Ext.container.Container.
 */
interface Ext_form_Basic extends Ext_util_Observable {

    owner : Ext_container_Container;

    new(owner:Ext_container_Container, config?:Object): Ext_form_Basic;

    applyIfToFields(obj:Object): Ext_form_Basic;

    applyToFields(obj:Object): Ext_form_Basic;

    checkDirty(): void;

    checkValidity(): void;

    clearInvalid(): Ext_form_Basic;

    destroy(): void;

    doAction(action:string, options?:Ext_form_Basic_ActionOptions): Ext_form_Basic;
    doAction(action:Ext_form_action_Action, options?:Ext_form_Basic_ActionOptions): Ext_form_Basic;

    findField(id:string): Ext_form_field_Field;

    getFieldValues(dirtyOnly?:boolean): Object;

    getFields(): Ext_util_MixedCollection;

    getRecord(): Ext_data_Model;

    getValues(asString?:boolean, dirtyOnly?:boolean, includeEmptyText?:boolean, useDataValues?:boolean): any;     // String/Object1

    hasInvalidField(): boolean;

    hasUpload(): boolean;

    isDirty(): boolean;

    isValid(): boolean;

    load(options:Object): Ext_form_Basic;

    loadRecord(record:Ext_data_Model): Ext_form_Basic;

    markInvalid(errors:Object): Ext_form_Basic;
    markInvalid(errors:Object[]): Ext_form_Basic;
    markInvalid(errors:Ext_data_Errors): Ext_form_Basic;

    reset(resetRecord?:boolean) : Ext_form_Basic;

    setValues(values:Object): Ext_form_Basic;
    setValues(values:Object[]): Ext_form_Basic;

    submit(options): Ext_form_Basic;

    updateRecord(record?:Ext_data_Model): Ext_form_Basic;

}


/**
 *      A container for grouping sets of fields, rendered as a HTML fieldset element.
 */
interface Ext_form_FieldSet extends Ext_container_Container {

    checkboxCmp: Ext_form_field_Checkbox;

    legend : Ext_Component;

    maskOnDisable : boolean;

    toggleCmp : Ext_panel_Tool;


    new(config?:Object): Ext_form_FieldSet;


    collapse(): Ext_form_FieldSet;

    expand(): Ext_form_FieldSet;

    getState(): Object;

    setTitle(title:string): Ext_form_FieldSet;

    toggle(): void;

}


/*      Layout package      */


/**
 *      This class manages state information for a component or element during a layout.
 */
interface Ext_layout_ContextItem extends Ext_Base {

    state : Object;
    wrapsComponent: boolean;

    addCls(newCls:string): void;
    addCls(newCls:string[]): void;

    block(layout:Ext_layout_Layout, propName:string): void;

    clearMarginCache(): void;

    domBlock(layout:Ext_layout_Layout, propName:string): void;

    flush(): void;

    getBorderInfo(): {
        left: number;
        top: number;
        right: number;
        bottom: number;
    };

    getClassList(): Object;

    getDomProp(propName:string): Object;

    getEl(nameOrEl:Object, owner:Object): Object;

    getFrameInfo(): Object;

    getMarginInfo(): Object;

    getPaddingInfo(): Object;

    getProp(propName:string): Object;

    getStyle(styleName:string): Object;

    getStyles(styleNames:string[], altNames?:string[]): Object;

    hasDomProp(propName:string): boolean;

    hasProp(propName:string): boolean;

    invalidate(options:{
        state?: Object;
        before?: (item:Ext_layout_ContextItem, options:Object) => any;
        after?: (item:Ext_layout_ContextItem, options:Object) => any;
        scope?: Object;
    }): void;

    recoverProp(propName:string, oldProps:Object, oldDirty:Object): void;

    removeCls(removeCls:string): void;
    removeCls(removeCls:string[]): void;

    setAttribute(name:string, value:Object): void;

    setContentHeight(height:number, measured:Object): void;

    setContentSize(width:number, height:number, measured:Object): void;

    setContentWidth(width:number, measured:Object): void;

    setHeight(height:number, dirty?:boolean): number;

    setProp(propName:string, value:Object, dirty:boolean): number;

    setWidth(width:number, dirty?:boolean): number;

}


/**
 *      Base Layout class - extended by ComponentLayout and ContainerLayout
 */
interface Ext_layout_Layout extends Ext_Base {

    done: boolean;
    isLayout : boolean;


    new(config?:Object): Ext_layout_Layout;


    beginLayout(ownerContext:Ext_layout_ContextItem): void;

    beginLayoutCycle(ownerContext:Ext_layout_ContextItem): void;

    calculate(ownerContext:Ext_layout_ContextItem): void;

    completeLayout(ownerContext:Ext_layout_ContextItem): void;

    finalizeLayout(ownerContext:Ext_layout_ContextItem): void;

    finishedLayout(ownerContext:Ext_layout_ContextItem): void;

    notifyOwner(ownerContext:Ext_layout_ContextItem): void;

    onContentChange(child:Ext_Component): boolean;

}


/**
 *      This class is intended to be extended or created via the layout configuration property.
 */
interface Ext_layout_component_Component extends Ext_layout_Layout {

    getRenderTarget(): Ext_dom_Element;

    getTarget(): Ext_dom_Element;

}


/**
 *      The class is the default component layout for Ext.Component when no explicit componentLayout is configured.
 */
interface Ext_layout_component_Auto extends Ext_layout_component_Component {

    beginLayoutCycle(ownerContext:Ext_layout_ContextItem): void;

    calculate(ownerContext:Ext_layout_ContextItem): void;

}


/**
 *      This class is intended to be extended or created via the layout configuration property.
 */
interface Ext_layout_container_Container extends Ext_layout_Layout {

    getScrollRangeFlags: Object;
    overflowPadderEl : Ext_dom_Element;

    calculateOverflow(ownerContext:Ext_layout_ContextItem, containerSize:Object, dimensions:number): void;

    doRenderPadder(out:Object, renderData:Object): void;

    getElementTarget(): Ext_dom_Element;

    getLayoutItems(): Ext_Component[];

    getRenderTarget(): Ext_dom_Element;

    getTarget(): Ext_dom_Element;

}


/**
 *      The AutoLayout is the default layout manager delegated by Ext.container.Container
 *      to render any child Components when no layout is configured into a Container.
 */
interface Ext_layout_container_Auto extends Ext_layout_container_Container {

    calculate(ownerContext:Ext_layout_ContextItem): void;

}


/**
 *      Base Class for HBoxLayout and VBoxLayout Classes. Generally it should not need to be used directly.
 */
interface Ext_layout_container_Box extends Ext_layout_container_Container {

    new(config?:Object):Ext_layout_container_Box;

    beginLayout(ownerContext:Ext_layout_ContextItem): void;

    beginLayoutCycle(ownerContext:Ext_layout_ContextItem): void;

    calculate(ownerContext:Ext_layout_ContextItem): void;

    completeLayout(ownerContext:Ext_layout_ContextItem): void;

    finishedLayout(ownerContext:Ext_layout_ContextItem): void;

    getElementTarget(): Ext_dom_Element;

    getRenderTarget(): Ext_dom_Element;

}


/**
 *      A layout that arranges items vertically down a Container.
 *      This layout optionally divides available vertical space between child items containing a numeric flex configuration.
 */
interface Ext_layout_container_VBox extends Ext_layout_container_Box {

    // nothing here

}


/**
 *      A layout that arranges items horizontally across a Container.
 *      This layout optionally divides available horizontal space between child items containing a numeric flex configuration.
 */
interface Ext_layout_container_HBox extends Ext_layout_container_Box {

    // nothing here

}


/**
 *      This is a base class for layouts that contain a single item that automatically expands to fill the layout's container.
 */
interface Ext_layout_container_Fit extends Ext_layout_container_Container {

    beginLayoutCycle(ownerContext:Ext_layout_ContextItem): void;

    calculate(ownerContext:Ext_layout_ContextItem): void;

}


/**
 *      This layout manages multiple child Components, each fitted to the Container,
 *      where only a single child Component can be visible at any given time.
 */
interface Ext_layout_container_Card extends Ext_layout_container_Fit {

    getActiveItem(): Ext_Component;

    getNext(): Ext_Component;

    getPrev(): Ext_Component;

    next(): Ext_Component;

    prev(): Ext_Component;

    setActiveItem(newCard:Ext_Component): Ext_Component;
    setActiveItem(newCard:number): Ext_Component;
    setActiveItem(newCard:string): Ext_Component;

}


/**
 *      This is a multi-pane, application-oriented UI layout style that supports multiple nested panels,
 *      automatic bars between regions and built-in expanding and collapsing of regions.
 */
interface Ext_layout_container_Border extends Ext_layout_container_Container {

    beginLayout(ownerContext:Ext_layout_ContextItem): void;

    calculate(ownerContext:Ext_layout_ContextItem): void;

    getLayoutItems(): Ext_Component[];

}


/*      App package     */


/**
 *      Controllers are the glue that binds an application together.
 *      All they really do is listen for events (usually from views) and take some action.
 */
interface Ext_app_Controller extends Ext_Base, Ext_util_Observable {

    addRef(ref:Object): void;

    control(selectors:string, listeners:Object): void;
    control(selectors:Object): void;

    getApplication(): Ext_app_Application;

    getController(name:string): Ext_app_Controller;

    getModel(name:string): Ext_data_Model;

    getStore(name:string): Ext_data_Store;

    getView(name:string) : Ext_Base;

    hasRef(ref:Object): boolean;

    init(application:Ext_app_Application): void;

    onLaunch(application:Ext_app_Application): void;

}


/**
 *      Represents an Ext JS 4 application, which is typically a single page app using a Viewport.
 */
interface Ext_app_Application extends Ext_app_Controller {

    control(selectors:string, listeners:Object): void;
    control(selectors:Object): void;

    getApplication(): Ext_app_Application;

    getController(name:string): Ext_app_Controller;

    getModel(name:string): Ext_data_Model;

    getStore(name:string): Ext_data_Store;

    getView(name:string): Ext_Base;

    launch(profile:string): boolean;

}


/*      Util package        */


interface Ext_util_Observable_ListenerOptions {
    scope?: Object;
    delay?: number;
    single?: boolean;
    buffer?: number;
    target?: Ext_util_Observable;
    element?: string;
    destroyable?: boolean;
}


/**
 *      Base class that provides a common interface for publishing events.
 */
interface Ext_util_Observable extends Ext_Base {

    hasListeners: Object;
    isObservable: boolean;

    new(config?:Object):Ext_util_Observable;

    addEvents(...eventNames:string[]): void;
    addEvents(eventNames:Object): void;

    addListener(eventName:string, fn?:Function, scope?:Object, options?:Ext_util_Observable_ListenerOptions): Object;
    addListener(eventName:Object, fn?:Function, scope?:Object, options?:Ext_util_Observable_ListenerOptions): Object;

    addManagedListener(item:Ext_util_Observable, ename:any /* Object/String */, fn?:Function, scope?:Object,
                       options?:Ext_util_Observable_ListenerOptions): Object;
    addManagedListener(item:Ext_dom_Element, ename:any /* Object/String */, fn?:Function, scope?:Object,
                       options?:Ext_util_Observable_ListenerOptions): Object;

    clearListeners(): void;

    clearManagedListeners(): void;

    enableBubble(eventNames:string): void;
    enableBubble(eventNames:string[]): void;

    fireEvent(eventName:string, ...args:Object[]): boolean;

    hasListener(eventName:string): boolean;

    mon(item:Ext_util_Observable, ename:any /* Object/String */, fn?:Function, scope?:Object,
        options?:Ext_util_Observable_ListenerOptions): Object;
    mon(item:Ext_dom_Element, ename:any /* Object/String */, fn?:Function, scope?:Object,
        options?:Ext_util_Observable_ListenerOptions): Object;

    mun(item:Ext_util_Observable, ename:any /* Object/String */, fn?:Function, scope?:Object): void;
    mun(item:Ext_dom_Element, ename:any /* Object/String */, fn?:Function, scope?:Object): void;

    on(eventName:string, fn?:Function, scope?:Object, options?:Ext_util_Observable_ListenerOptions): Object;
    on(eventName:Object, fn?:Function, scope?:Object, options?:Ext_util_Observable_ListenerOptions): Object;

    relayEvents(origin:Object, events:string[], prefix?:string): Object;

    removeListener(eventName:string, fn:Function, scope?:Object): void;

    removeManagedListener(item:Ext_util_Observable, ename:any /* Object/String */, fn?:Function, scope?:Object): void;
    removeManagedListener(item:Ext_dom_Element, ename:any /* Object/String */, fn?:Function, scope?:Object): void;

    resumeEvents(): void;

    suspendEvents(queueSuspended:boolean): void;

    un(eventName:string, fn:Function, scope?:Object): void;

    capture(o:Ext_util_Observable, fn:Function, scope?:Object): void;

    observe(c:Function, listeners:Object): void;

    releaseCapture(o:Ext_util_Observable): void;

}


/**
 *      A mixin for being able to save the state of an object to an underlying Ext.state.Provider.
 */
interface Ext_state_Stateful extends Ext_Base, Ext_util_Observable {

    addStateEvents(events:string): void;
    addStateEvents(events:string[]): void;

    applyState(state:Object): void;

    destroy(): void;

    getState(): Object;

    savePropToState(propName:string, state:Object, stateName?:string): boolean;

    savePropsToState(propNames:string, state:Object): Object;
    savePropsToState(propNames:string[], state:Object): Object;

    saveState(): void;

}


/**
 *      Ext.util.Animate provides an API for the creation of animated transitions of properties and styles
 */
interface Ext_util_Animate extends Ext_Base {

    animate(config): Object;

    getActiveAnimation(): any /* Ext.fx.Anim/boolean */;

    sequenceFx(): Object;

    stopAnimation(): Ext_dom_Element;

    syncFx(): Object;

}


/**
 *  Utility class for setting/reading values from browser cookies.
 */
interface Ext_util_Cookies extends Ext_Base {

    clear(name:string, path?:string): void;

    get(name:string): Object;

    set(name:string, value:Object, expires?:Object, path?:string, domain?:string, secure?:boolean): void;

}


interface Ext_util_Renderable extends Ext_Base {

    doAutoRender(): void;

    ensureAttachedToBody(runLayout?:boolean): void;

    getInsertPosition(position): Html_dom_Element;

    render(container?:string, position?:any /* String/Number */): void;
    render(container?:Html_dom_Element, position?:any /* String/Number */): void;
    render(container?:Ext_dom_Element, position?:any /* String/Number */): void;
}


/**
 *      A mixin to add floating capability to a Component.
 */
interface Ext_util_Floating extends Ext_Base {

    alignTo(element:string, position?:string, offsets?:number[]): Ext_Component;
    alignTo(element:Ext_Component, position?:string, offsets?:number[]): Ext_Component;
    alignTo(element:Ext_dom_Element, position?:string, offsets?:number[]): Ext_Component;
    alignTo(element:Html_dom_Element, position?:string, offsets?:number[]): Ext_Component;

    center(): Ext_Component;

    doConstrain(constrainTo?:string): void;
    doConstrain(constrainTo?:Html_dom_Element): void;
    doConstrain(constrainTo?:Ext_dom_Element): void;
    doConstrain(constrainTo?:Ext_util_Region): void;

    setActive(active?:boolean, newActive?:Ext_Component): void;

    toBack(): Ext_Component;

    toFront(preventFocus?:boolean): Ext_Component;
}


/**
 *      This mixin enables classes to declare relationships to child elements and
 *      provides the mechanics for acquiring the elements and storing them on an object instance as properties.
 */
interface Ext_util_ElementContainer extends Ext_Base {

    addChildEls(...children:any[]): void;

    removeChildEls(testFn:Function): void;

}


interface Ext_util_AbstractMixedCollection extends Ext_Base, Ext_util_Observable {

    isMixedCollection : boolean;

    add(key:string, obj?:Object): Object;

    addAll(obj:Object): void;
    addAll(obj:Object[]): void;

    clear(): void;

    clone(): Ext_util_MixedCollection;

    collect(property:string, root?:string, allowBlank?:boolean): Object[];

    contains(obj:Object): boolean;

    containsKey(key:string): boolean;

    each(fn:(item:Object, index:number, length:number) => any, scope?:Object): void;

    eachKey(fn:(key:string, item:Object, index:number, length:number) => any, scope?:Object): void;

    filter(property:string, value:string, anyMatch?:boolean, caseSensitive?:boolean): Ext_util_MixedCollection;
    filter(property:Ext_util_Filter[]): Ext_util_MixedCollection;

    filterBy(fn:(item:Object, key:string) => any, scope?:Object): Ext_util_MixedCollection;

    findBy(fn:(item:Object, key:string) => any, scope?:Object): Object;

    findIndex(property:string, value:string, start?:number, anyMatch?:boolean, caseSensitive?:boolean): number;

    findIndexBy(fn:(item:Object, key:string) => any, scope?:Object, start?:number): number;

    first(): Object;

    get(key:string): Object;
    get(key:number): Object;

    getAt(index:number): Object;

    getByKey(key:string): Object;

    getCount(): number;

    getKey(item:Object): string;

    getRange(startIndex?:number, endIndex?:number): Object[];

    indexOf(obj:Object): number;

    indexOfKey(key:string): number;

    insert(index:number, key:string, obj?:Object): Object;

    last(): Object;

    remove(obj:Object): Object;

    removeAll(items:Object[]): Ext_util_MixedCollection;

    removeAt(index:number): Object;

    removeAtKey(key:string): Object;

    replace(key:string, obj:Object): Object;

    sum(property:string, root?:string, start?:number, end?:number): number;

}


/**
 *      Represents a collection of a set of key and value pairs.
 */
interface Ext_util_MixedCollection extends Ext_util_AbstractMixedCollection, Ext_util_Sortable {

    findInsertionIndex(newItem:Object, sorterFn?:Function): number;

    reorder(mapping:Object): void;

    sortBy(sorterFn:Function): void;

    sortByKey(direction?:string, fn?:Function): void;

}


/**
 *      Represents a filter that can be applied to a MixedCollection.
 */
interface Ext_util_Filter {

    // nothing here

}


/**
 *      This class represents a rectangular region in X,Y space, and performs geometric transformations or tests upon the region.
 */
interface Ext_util_Region extends Ext_Base {

    adjust(top:number, right:number, bottom:number, left:number): Ext_util_Region;

    constrainTo(targetRegion:Ext_util_Region): Ext_util_Region;

    contains(region:Ext_util_Region): boolean;

    copy(): Ext_util_Region;

    copyFrom(source:Ext_util_Region): Ext_util_Region;

    equals(region:Ext_util_Region): boolean;

    getOutOfBoundOffset(axis?:string, p?:Ext_util_Point): Ext_util_Offset;

    getOutOfBoundOffsetX(p:number): number;

    getOutOfBoundOffsetY(p:number): number;

    intersect(region:Ext_util_Region): any;       // Ext.util.Region/boolean

    isOutOfBound(axis?:string, p?:Ext_util_Point): boolean;
    isOutOfBound(axis?:string, p?:number): boolean;

    isOutOfBoundX(p:number): boolean;

    isOutOfBoundY(p:number): boolean;

    round(): Ext_util_Region;

    translateBy(x:number, y:number): Ext_util_Region;
    translateBy(x:Object): Ext_util_Region;

    union(region:Ext_util_Region): Ext_util_Region;

}


/**
 *      Represents a 2D point with x and y properties, useful for comparison and instantiation from an event
 */
interface Ext_util_Point extends Ext_util_Region {

    equals(point:Ext_util_Point): boolean;
    equals(point:{
        left: number;
        top: number;
    }): boolean;

    isWithin(point:Ext_util_Point, threshold:number): boolean;
    isWithin(point:{
        left: number;
        top: number;
    }, threshold:number): boolean;

    roundedEquals(point:Ext_util_Point): boolean;
    roundedEquals(point:{
        left: number;
        top: number;
    }): boolean;

    toString(): string;

    translate(x:number, y:number) : Ext_util_Region;
    translate(x:{
        x: number;
        y: number;
    }) : Ext_util_Region;

}


interface Ext_util_Offset extends Ext_Base {

    // nothing here

}


/**
 *      A mixin which allows a data component to be sorted. This is used by e.g. Ext.data.Store and Ext.data.TreeStore.
 */
interface Ext_util_Sortable extends Ext_Base {

    isSortable: boolean;

    sorters : Ext_util_MixedCollection;

    generateComparator(): void;

    initSortable(): void;

    sort(sorters?:string, direction?:string): Ext_util_Sorter[];
    sort(sorters?:Ext_util_Sorter[], direction?:string): Ext_util_Sorter[];

    createComparator(sorters:Ext_util_Sorter[]): Function;

}


/**
 *      Represents a single sorter that can be applied to a Store.
 */
interface Ext_util_Sorter extends Ext_Base {

    setDirection(direction:string): void;

    toggle(): void;

    updateSortFunction(fn?:Function): void;

}


/**
 *      Provides the ability to execute one or more arbitrary tasks in a asynchronous manner.
 */
interface Ext_util_TaskRunner extends Ext_Base {

    new(interval?:number): Ext_util_TaskRunner;
    new(interval?:Object): Ext_util_TaskRunner;

    destroy(): void;

    newTask(config:Object): void;

    start(task:{
        run?: () => boolean;
        onError?: Function;
        interval?: number;
        args?: Object[];
        scope?: Object;
        duration?: number;
        repeat?: number;
    }): Object;

    stop(task:Object): Object;

    stopAll(): void;

}


/*      Data package        */


/**
 *      AbstractStore is a superclass of Ext.data.Store and Ext.data.TreeStore.
 */
interface Ext_data_AbstractStore extends Ext_Base, Ext_util_Observable, Ext_util_Sortable {

    defaultProxyType : string;
    filters : Ext_util_MixedCollection;
    isDestroyed : boolean;
    isStore : boolean;

    getModifiedRecords(): Ext_data_Model[];

    getNewRecords(): Ext_data_Model[];

    getProxy(): Ext_data_proxy_Proxy;

    getRemovedRecords(): Ext_data_Model[];

    getUpdatedRecords(): Ext_data_Model[];

    isLoading(): boolean;

    load(options?:Object): Ext_data_Store;

    reload(options:Object): void;

    removeAll(): void;

    resumeAutoSync(): void;

    setProxy(proxy:string): Ext_data_proxy_Proxy;
    setProxy(proxy:Object): Ext_data_proxy_Proxy;
    setProxy(proxy:Ext_data_proxy_Proxy): Ext_data_proxy_Proxy;

    suspendAutoSync(): void;

    sync(options:{
        batch?: Ext_data_Batch;
        callback?: (batch:Ext_data_Batch, options:Object) => any;
        success?: (batch:Ext_data_Batch, options:Object) => any;
        failure?: (batch:Ext_data_Batch, options:Object) => any;
        scope?: Object;
    }): Ext_data_Store;

}


/**
 *      The Store class encapsulates a client side cache of Model objects.
 */
interface Ext_data_Store extends Ext_data_AbstractStore {

    currentPage: number;
    data: Ext_util_MixedCollection;
    pageMap: Ext_data_Store_PageMap;
    snapshot: Ext_util_MixedCollection;


    new(config?:Object): Ext_data_Store;


    add(model:Ext_data_Model) : Ext_data_Model[];
    add(model:Ext_data_Model[]) : Ext_data_Model[];

    addSorted(model:Ext_data_Model): void;

    aggregate(fn:Function, scope?:Object, grouped?:boolean, args?:any[]): Object;

    average(field:string, grouped?:boolean): Object;

    clearFilter(suppressEvent?:boolean): void;

    clearGrouping(): void;

    collect(dataIndex:string, allowNull?:boolean, bypassFilter?:boolean): Object[];

    commitChanges(): void;

    count(grouped?:boolean): number;

    each(fn:Function, scope?:Object): void;

    filter(filters:string, value:Object): void;
    filter(filters:Object): void;
    filter(filters:Ext_util_Filter): void;

    filterBy(fn:(model:Ext_data_Model, id:string) => any, scope?:Object): void;

    find(fieldName:string, value:Object, startIndex?:number, anyMatch?:boolean, caseSensitive?:boolean, exactMatch?:boolean): number;

    findBy(fn:(model:Ext_data_Model, id:string) => any, scope?:Object, startIndex?:number): number;

    findExact(fieldName:string, value:Object, startIndex?:number): number;

    findRecord(fieldName:string, value:Object, startIndex?:number, anyMatch?:boolean, caseSensitive?:boolean,
               exactMatch?:boolean): Ext_data_Model;

    first(grouped?:boolean): Ext_data_Model;        //Ext.data.Model/undefined

    getAt(index:number): Ext_data_Model;

    getById(id:string): Ext_data_Model;

    getCount(): number;

    getGroupString(instance:Ext_data_Model): string;

    getGroups(groupName?:string): any;         // Object/Object[]

    getPageFromRecordIndex(index:number): number;

    getRange(startIndex?:number, endIndex?:number) : Ext_data_Model[];

    getTotalCount(): number;

    group(groupers:string, direction?:string): void;
    group(groupers:string[], direction?:string): void;

    guaranteeRange(start:number, end:number, callback:Function, scope:Object, options:Object): void;

    indexOf(model:Ext_data_Model): number;

    indexOfId(id:string): number;

    indexOfTotal(model:Ext_data_Model): number;

    insert(index, model:Ext_data_Model): Ext_data_Model[];

    isFiltered(): boolean;

    isGrouped(): boolean;

    last(grouped?:boolean): Ext_data_Model;      // Ext.data.Model/undefined

    loadData(data:Object[], append?:boolean): void;
    loadData(data:Ext_data_Model[], append?:boolean): void;

    loadPage(page:number, options?:Object): void;

    loadRawData(data:Object[], append?:boolean): void;

    loadRecords(records:Ext_data_Model[], options:{
        addRecords?: boolean;
        start?: number;
    }): void;

    max(field:string, grouped?:boolean): Object;

    min(field:string, grouped?:boolean): Object;

    nextPage(options:Object): void;

    prefetch(options:Object): void;

    prefetchPage(page:number, options:Object): void;

    prefetchRange(start:number, end:number): void;

    previousPage(options:Object): void;

    query(property:string, value:string, anyMatch?:boolean, caseSensitive?:boolean, exactMatch?:boolean): Ext_util_MixedCollection;

    queryBy(fn:(model:Ext_data_Model) => any, scope?:Object): Ext_util_MixedCollection;

    rejectChanges(): void;

    remove(model:Ext_data_Model): void;
    remove(model:Ext_data_Model[]): void;

    //removeAll(silent:boolean): void;

    removeAt(index:number, count?:number): void;

    sort(sorters?:string, direction?:string): Ext_util_Sorter[];
    sort(sorters?:Ext_util_Sorter[], direction?:string): Ext_util_Sorter[];

    sum(field:string, grouped?:boolean): number;

}


/**
 *      The TreeStore is a store implementation that is backed by by an Ext.data.Tree.
 */
interface Ext_data_TreeStore extends Ext_data_AbstractStore {

    fields : Object;

    new(config?:Object): Ext_data_TreeStore;

    getNewRecords(): Ext_data_Model[];

    getNodeById(id:Object): Ext_data_NodeInterface;

    getRootNode(): Ext_data_NodeInterface;

    getUpdatedRecords(): Ext_data_Model[];

    //load(options?:Object): void;

    removeAll(): void;

    setProxy(proxy:string): Ext_data_proxy_Proxy;
    setProxy(proxy:Object): Ext_data_proxy_Proxy;
    setProxy(proxy:Ext_data_proxy_Proxy): Ext_data_proxy_Proxy;

    setRootNode(root:Ext_data_Model): Ext_data_NodeInterface;
    setRootNode(root:Ext_data_NodeInterface): Ext_data_NodeInterface;
    setRootNode(root:Object): Ext_data_NodeInterface;

}


/**
 *      Proxies are used by Stores to handle the loading and saving of Model data.
 */
interface Ext_data_proxy_Proxy extends Ext_Base {

    batch(options:{
        operations?: Object;
        listeners?: Object;
        batch: Ext_data_Batch;
        callback: (batch:Ext_data_Batch, options:Object) => any;
        success: (batch:Ext_data_Batch, options:Object) => any;
        failure: (batch:Ext_data_Batch, options:Object) => any;
        scope: Object;
    }) : Ext_data_Batch;

    //create(operation:Ext_data_Operation, callback:Function, scope:Object): void;

    destroy(operation:Ext_data_Operation, callback:Function, scope:Object): void;

    getModel() : Ext_data_Model;

    getReader() : Ext_data_reader_Reader;

    getWriter() : Ext_data_writer_Writer;

    read(operation:Ext_data_Operation, callback:Function, scope:Object): void;

    setModel(model:string, setOnStore:boolean): void;
    setModel(model:Ext_data_Model, setOnStore:boolean): void;

    setReader(reader:string) : Ext_data_reader_Reader;
    setReader(reader:Object) : Ext_data_reader_Reader;
    setReader(reader:Ext_data_reader_Reader) : Ext_data_reader_Reader;

    setWriter(writer:string) : Ext_data_writer_Writer;
    setWriter(writer:Object) : Ext_data_writer_Writer;
    setWriter(writer:Ext_data_writer_Writer) : Ext_data_writer_Writer;

    update(operation:Ext_data_Operation, callback:Function, scope:Object): void;

}


interface Ext_data_proxy_Server extends Ext_data_proxy_Proxy {

    buildRequest(operation:Ext_data_Operation): Ext_data_Request;

    buildUrl(request:Ext_data_Request): string;

    //create(): void;

    destroy(): void;

    doRequest(operation:Ext_data_Operation, callback:Function, scope:Object): void;

    encodeFilters(filters:Ext_util_Filter[]) : string;

    encodeSorters(sorters:Ext_util_Sorter[]) : string;

    read(): void;

    setExtraParam(name:string, value:any): void;

    update(): void;

}


interface Ext_data_proxy_Ajax extends Ext_data_proxy_Server {

    actionMethods : Object;

    new(config?:Object): Ext_data_proxy_Ajax;

    getMethod(request:Ext_data_Request): string;
}


interface Ext_data_proxy_Rest extends Ext_data_proxy_Ajax {

    new(config?:Object): Ext_data_proxy_Rest;

    buildUrl(request:Ext_data_Request):string;
}


/**
 *      A Model represents some object that your application manages.
 */
interface Ext_data_Model extends Ext_util_Observable {

    COMMIT: string;
    EDIT: string;
    REJECT: string;

    dirty: boolean;
    editing: boolean;
    fields: Ext_util_MixedCollection;
    isModel: boolean;
    modified: Object;
    phantom: boolean;
    raw: Object;
    data: Object;
    store: Ext_data_Store;
    stores: Ext_data_Store[];

    beginEdit(): void;

    cancelEdit(): void;

    commit(silent?:boolean): void;

    copy(id?:string): Ext_data_Model;

    destroy(options:Object): Ext_data_Model;

    endEdit(silent:boolean, modifiedFieldNames:string[]): void;

    get(fieldName:string): Object;

    getAssociatedData(): Object;

    getChanges(): Object;

    getData(includeAssociated?:boolean): Object;

    getId(): string;        // Number/String

    getProxy(): Ext_data_proxy_Proxy;

    isModified(fieldName:string): boolean;

    isValid(): boolean;

    join(store:Ext_data_Store): void;

    reject(silent?:boolean): void;

    save(options:Object): Ext_data_Model;

    set(fieldName:string, newValue:Object): string[];

    setDirty(): void;

    setId(id:string): void;

    setProxy(proxy:Ext_data_proxy_Proxy): Ext_data_proxy_Proxy;

    unjoin(store:Ext_data_Store): void;

    validate() : Ext_data_Errors;

}


/**
 *      Wraps a collection of validation error responses and provides convenient functions for accessing and errors for specific fields.
 */
interface Ext_data_Errors extends Ext_Base {

    getByField(fieldName:string): Object[];

    isValid(): boolean;

}


/**
 * This class is used as a set of methods that are applied to the prototype of a Model to decorate it with a Node API.
 */
interface Ext_data_NodeInterface extends Ext_Base {

    childNodes: Object;

    firstChild: Object;

    isNode: boolean;

    lastChild: Object;

    nextSibling: Object;

    parentNode: Object;

    previousSibling: Object;


    appendChild(node:Ext_data_NodeInterface): Ext_data_NodeInterface;
    appendChild(node:Ext_data_NodeInterface[]): Ext_data_NodeInterface;

    bubble(fn:Function, scope?:Object, args?:any[]): void;

    cascadeBy(fn, scope?:Object, args?:any[]): void;

    collapse(recursive?:boolean, callback?:Function, scope?:Object): void;

    collapseChildren(recursive?:boolean, callback?:Function, scope?:Object): void;

    contains(node): boolean;

    copy(id?:string, deep?:boolean): Ext_data_NodeInterface;

    createNode(node:Object): Ext_data_NodeInterface;

    destroy(silent?:boolean): void;

    eachChild(fn:Function, scope?:Object, args?:any[]): void;

    expand(recursive?:boolean, callback?:Function, scope?:Object): void;

    expandChildren(recursive?:boolean, callback?:Function, scope?:Object): void;

    findChild(attribute:string, value:Object, deep?:boolean): Ext_data_NodeInterface;

    findChildBy(fn, scope?:Object, deep?:boolean): Ext_data_NodeInterface;

    getChildAt(index): Ext_data_NodeInterface;

    getDepth(): number;

    getInitialConfig(name?:string): any;

    getOwnerTree(): Ext_tree_Panel;

    getPath(field?:string, separator?:string): string;

    hasChildNodes(): boolean;

    indexOf(node): number;

    indexOfId(id): number;

    insertBefore(node:Ext_data_NodeInterface, refNode:Ext_data_NodeInterface): Ext_data_NodeInterface;

    insertChild(index:number, node:Ext_data_NodeInterface): Ext_data_NodeInterface;

    isAncestor(node:Ext_data_NodeInterface): boolean;

    isExpandable(): boolean;

    isExpanded(): boolean;

    isFirst(): boolean;

    isLast(): boolean;

    isLeaf(): boolean;

    isLoaded(): boolean;

    isLoading(): boolean;

    isRoot(): boolean;

    isVisible(): boolean;

    remove(destroy?:boolean): Ext_data_NodeInterface;

    removeAll(destroy?:boolean): Ext_data_NodeInterface;

    removeChild(node:Ext_data_NodeInterface, destroy?:boolean): Ext_data_NodeInterface;

    replaceChild(newChild, oldChild): Ext_data_NodeInterface;

    sort(fn, recursive?:boolean, suppressEvent?:boolean): void;

    updateInfo(commit): boolean;

}


/*      Managers        */


/**
 *      Base Manager class
 */
interface Ext_AbstractManager extends Ext_Base {

    all: Ext_util_HashMap;

    //create(config:Object, defaultType:string): Object;

    each(fn:(key:string, value:number, length:number) => boolean, scope:Object): void;

    get(id:string): Object;

    getCount(): number;

    isRegistered(type:string): boolean;

    onAvailable(id:string, fn:Function, scope:Object): void;

    register(item:Object): void;

    registerType(type:string, cls:Function): void;

    unregister(item:Object): void;

}


/**
 *  Ext.ClassManager manages all classes and handles mapping from string class name
 *  to actual class objects throughout the whole framework.
 */
interface Ext_ClassManager {

    addNameAliasMappings(aliases:Object): Ext_ClassManager;

    addNameAlternateMappings(alternates:Object): Ext_ClassManager;

    get(name:string): Ext_Class;

    getAliasesByName(name:string): any[];

    getByAlias(alias:string): Ext_Class;

    getClass(object:any): Ext_Class;

    getDisplayName(object:any): string;

    getName(object:any): string;

    getNameByAlias(alias:string): string;

    getNameByAlternate(alternate:string): string;

    getNamesByExpression(expression:string): string[];

    instantiateByAlias(alias:string, ...args:any[]): any;

    isCreated(className:string): boolean;

    set(name:string, value:any): Ext_ClassManager;

    setAlias(cls:string, alias:string): Ext_ClassManager;
    setAlias(cls:Ext_Class, alias:string): Ext_ClassManager;

    setNamespace(name:string, value:any): void;
}

/**
 *      Provides a registry of all Components (instances of Ext.Component or any subclass thereof) on a page so that they
 *      can be easily accessed by component id (see get, or the convenience method Ext.getCmp).
 */
interface Ext_ComponentManager extends Ext_AbstractManager {

    //create(config:Object, defaultType?:string): Ext_Component;

    registerType(type:string, cls:Function): void;

}


interface Ext_EventManager_ListenerOptions {
    scope?: Object;
    delegate?: string;
    stopEvent?: boolean;
    preventDefault?: boolean;
    stopPropagation?: boolean;
    normalized?: boolean;
    delay?: number;
    single?: boolean;
    buffer?: number;
    target?: Ext_dom_Element;
}

/**
 *      Registers event handlers that want to receive a normalized EventObject
 *      instead of the standard browser event and provides several useful events directly.
 */
interface Ext_EventManager {

    idleEvent : Object;

    addListener(el:string, eventName:string, handler:(event:Ext_EventObject, target:Ext_dom_Element, options:Object) => any, scope?:Object,
                options?:Ext_EventManager_ListenerOptions): void;
    addListener(el:Html_dom_Element, eventName:string, handler:(event:Ext_EventObject, target:Ext_dom_Element, options:Object) => any,
                scope?:Object, options?:Ext_EventManager_ListenerOptions): void;

    getId(element:Html_dom_Element): string;
    getId(element:Ext_dom_Element): string;

    getKeyEvent(): string;

    getPageX(event:Object): number;

    getPageXY(event:Object): number[];

    getPageY(event:Object): number;

    getRelatedTarget(event:Object): Html_dom_Element;

    getTarget(event:Object): Html_dom_Element;

    on(el:string, eventName:string, handler:(event:Ext_EventObject, target:Ext_dom_Element, options:Object) => any, scope?:Object,
       options?:Ext_EventManager_ListenerOptions): void;
    on(el:Html_dom_Element, eventName:string, handler:(event:Ext_EventObject, target:Ext_dom_Element, options:Object) => any, scope?:Object,
       options?:Ext_EventManager_ListenerOptions): void;

    onDocumentReady(fn:Function, scope?:Object, options?:Ext_EventManager_ListenerOptions): void;

    onWindowResize(fn:Function, scope?:Object, options?:Ext_EventManager_ListenerOptions): void;

    onWindowUnload(fn:Function, scope?:Object, options?:Ext_EventManager_ListenerOptions): void;

    pollScroll(): void;

    preventDefault(event:Object): void;

    purgeElement(el:string, eventName?:string): void;
    purgeElement(el:Html_dom_Element, eventName?:string): void;

    removeAll(el:string): void;
    removeAll(el:Html_dom_Element): void;

    removeListener(el:string, eventName:string, fn:Function, scope:Object): void;
    removeListener(el:Html_dom_Element, eventName:string, fn:Function, scope:Object): void;

    removeResizeListener(fn:Function, scope:Object): void;

    removeUnloadListener(fn:Function, scope:Object): void;

    stopEvent(event:Object): void;

    stopPropagation(event:Object): void;

    un(el:string, eventName:string, fn:Function, scope:Object): void;
    un(el:Html_dom_Element, eventName:string, fn:Function, scope:Object): void;

}


/**
 *      A class that manages a group of Ext.Component.floating Components and provides
 *      z-order management, and Component activation behavior, including masking below the active (topmost) Component.
 */
interface Ext_ZIndexManager extends Ext_Base {

    bringToFront(comp:string): boolean;
    bringToFront(comp:Ext_Component): boolean;

    each(fn:Function, scope?:Object): void;

    eachBottomUp(fn:Function, scope?:Object): void;

    eachTopDown(fn:Function, scope?:Object): void;

    get(id:string): Ext_Component;

    getActive(): Ext_Component;

    getBy(fn:Function, scope?:Object): Ext_Component[];

    hideAll(): void;

    register(comp:Ext_Component): void;

    sendToBack(comp:Ext_Component): Ext_Component;
    sendToBack(comp:string): Ext_Component;

    unregister(comp:Ext_Component): void;

}


/**
 *      The default global floating Component group that is available automatically.
 */
interface Ext_WindowManager extends Ext_ZIndexManager {

}


/**
 *      Contains a collection of all stores that are created that have an identifier.
 */
interface Ext_data_StoreManager extends Ext_util_MixedCollection {

    //getKey(item:Object): Object;

    lookup(store:Ext_data_Store): Ext_data_Store;

    register(...stores:Ext_data_Store[]): void;

    unregister(...stores:Ext_data_Store[]): void;

}


/*      Direct package      */


/**
 *      Small utility class used internally to represent a Direct method.
 */
interface Ext_direct_RemotingMethod extends Ext_Base {

    new(config?:Object): Ext_direct_RemotingMethod;

    getCallData(args:any[]): Object;

}


/**
 *      Ext.direct.Provider is an abstract class meant to be extended.
 */
interface Ext_direct_Provider extends Ext_Base {

    connect(): void;

    disconnect(): void;

    isConnected(): void;

}


/**
 *      A base provider for communicating using JSON. This is an abstract class and should not be instanced directly.
 */
interface Ext_direct_JsonProvider extends Ext_direct_Provider {

    createEvent(response:Object): Ext_direct_Event;

}


/**
 *      The RemotingProvider exposes access to server side methods on the client (a remote procedure call (RPC) type of connection
 *      where the client can initiate a procedure on the server).
 */
interface Ext_direct_RemotingProvider extends Ext_direct_JsonProvider {

    namespace: string;

    new(config:Object): Ext_direct_RemotingProvider;

    connect(): void;

    disconnect(): void;

    isConnected(): boolean;

    createEvent(response:any): Ext_direct_Event;

    createHandler(action:any, method:any):Function;
}


/**
 *      A base class for all Ext.direct events. An event is created after some kind of interaction with the server
 */
interface Ext_direct_Event {

    new(config?:Object): Ext_direct_Event;

    getData(): Object;

}


/**
 *      Supporting Class for Ext.Direct (not intended to be used directly).
 */
interface Ext_direct_Transaction extends Ext_Base {

    new(config?:Object): Ext_direct_Transaction;

}


interface Ext_direct_Manager_listenerOptions {
    scope: Object;
    delay: number;
    single: boolean;
    buffer: number;
    target: Ext_util_Observable;
    element: string;
    destroyable? : boolean;
}

/**
 *      Ext.Direct aims to streamline communication between the client and server by providing a single interface that reduces the amount
 *      of common code typically required to validate data and handle returned data packets (reading data, error conditions, etc).
 */
interface Ext_direct_Manager extends Ext_Base {

    new(): Ext_direct_Manager;

    addEvents(eventNames:any /*Object/string*/): void;

    addListener(eventName:string, fn?:Function, scope?:Object, options?:Ext_direct_Manager_listenerOptions): Object;

    addManagedListener(item:Ext_util_Observable, ename:string, fn?:Function, scope?:Object,
                       options?:Ext_direct_Manager_listenerOptions): Object;
    addManagedListener(item:Ext_util_Observable, ename:Object, fn?:Function, scope?:Object,
                       options?:Ext_direct_Manager_listenerOptions): Object;

    addProvider(provider:Ext_direct_Provider): void;
    addProvider(provider:Object): void;

    clearListeners(): void;

    clearManagedListeners(): void;

    enableBubble(eventNames): void;

    fireEvent(eventName, args): boolean;

    getProvider(id:string): Ext_direct_Provider;
    getProvider(id:Ext_direct_Provider): Ext_direct_Provider;

    hasListener(eventName:string): boolean;

    mon(item:Ext_util_Observable, ename:string, fn?:Function, scope?:Object, options?:Ext_direct_Manager_listenerOptions): Object;

    mun(item:Ext_util_Observable, ename:string, fn?:Function, scope?:Object): void;

    on(eventName:string, fn?:Function, scope?:Object, options?:Ext_direct_Manager_listenerOptions): Object;

    relayEvents(origin:Ext_util_Observable, events:string[], prefix?:string): Object;

    removeListener(eventName, fn:Function, scope?:Object): void;

    removeManagedListener(item, ename, fn?:Function, scope?:Object): void;

    removeProvider(provider) : Ext_direct_Provider;

    resumeEvents(): void;

    suspendEvents(queueSuspended): void;

    un(eventName, fnFunction, scope?:Object): void;

}


/*      Drag and Drop package       */


/**
 *      Defines the interface and base operation of items that that can be dragged or can be drop targets.
 */
interface Ext_dd_DragDrop extends Ext_Base {

    available: boolean;

    config: Object;

    defaultPadding: Object;

    groups: Object;

    hasOuterHandles: boolean;

    id: string;

    ignoreSelf: boolean;

    invalidHandleClasses: string[];

    invalidHandleIds: Object;

    invalidHandleTypes: Object;

    isTarget: boolean;

    maintainOffset: boolean;

    moveOnly: boolean;

    padding: number[];

    primaryButtonOnly: boolean;

    xTicks: number[];

    yTicks: number[];


    new(id:string, sGroup:string, config:Object): Ext_dd_DragDrop;


    addInvalidHandleClass(cssClass:string): void;

    addInvalidHandleId(id:string): void;

    addInvalidHandleType(tagName:string): void;

    addToGroup(sGroup:string): void;

    applyConfig(): void;

    clearConstraints(): void;

    clearTicks(): void;

    constrainTo(constrainTo:string, pad?:any /* Object/number */, inContent?:boolean): void;
    constrainTo(constrainTo:Html_dom_Element, pad?:any /* Object/number */, inContent?:boolean): void;
    constrainTo(constrainTo:Ext_dom_Element, pad?:any /* Object/number */, inContent?:boolean): void;

    endDrag(e:Object): void;

    getDragEl(): Html_dom_Element;

    getEl(): Html_dom_Element;

    init(id:string, sGroup:string, config:Object): void;

    initTarget(id:string, sGroup:string, config:Object): void;

    isLocked(): boolean;

    isValidHandleChild(node:Html_dom_Element): boolean;

    lock(): void;

    onAvailable(): void;

    onDrag(e:Object): void;

    onDragDrop(e:Object, id:string): void;
    onDragDrop(e:Object, id:Ext_dd_DragDrop[]): void;

    onDragEnter(e:Object, id:string): void;
    onDragEnter(e:Object, id:Ext_dd_DragDrop[]): void;

    onDragOut(e:Object, id:string): void;
    onDragOut(e:Object, id:Ext_dd_DragDrop[]): void;

    onDragOver(e:Object, id:string): void;
    onDragOver(e:Object, id:Ext_dd_DragDrop[]): void;

    onInvalidDrop(e:Object): void;

    onMouseDown(e:Object): void;

    onMouseUp(e:Object): void;

    removeFromGroup(sGroup:string): void;

    removeInvalidHandleClass(cssClass:string): void;

    removeInvalidHandleId(id:string): void;

    removeInvalidHandleType(tagName:string): void;

    resetConstraints(maintainOffset?:boolean): void;

    setDragElId(id:string): void;

    setHandleElId(id:string): void;

    setInitPosition(diffX:number, diffY:number): void;

    setOuterHandleElId(id:string): void;

    setPadding(iTop:number, iRight:number, iBot:number, iLeft:number): void;

    setXConstraint(iLeft:number, iRight:number, iTickSize?:number): void;

    setYConstraint(iUp:number, iDown:number, iTickSize?:number): void;

    startDrag(X:number, Y:number): void;

    toString(): string;

    unlock(): void;

    unreg(): void;

}


/**
 *      A DragDrop implementation where the linked element follows the mouse cursor during a drag.
 */
interface Ext_dd_DD extends Ext_dd_DragDrop {

    scroll: boolean;

    new(id:string, sGroup?:string, config?:Object): Ext_dd_DD;

    alignElWithMouse(el:Html_dom_Element, iPageX:number, iPageY:number): void;

    applyConfig(): void;

    autoOffset(iPageX:number, iPageY:number): void;

    b4Drag(e:Object): void;

    b4MouseDown(e:Object): void;

    cachePosition(iPageX?:number, iPageY?:number): void;

    setDelta(iDeltaX:number, iDeltaY:number): void;

    setDragElPos(iPageX:number, iPageY:number): void;

    toString(): string;

}


/**
 *      A DragDrop implementation that inserts an empty, bordered div into the document that follows the cursor during drag operations.
 */
interface Ext_dd_DDProxy extends Ext_dd_DD {

    centerFrame: boolean;

    resizeFrame: boolean;

    dragElId: string;


    new(id:string, sGroup:string, config:Object): Ext_dd_DD;


    applyConfig(): void;

    b4MouseDown(e:Object): void;

    createFrame(): void;

    endDrag(e:Object): void;

    initFrame(): void;

    toString(): string;

}


/**
 *      A simple class that provides the basic implementation needed to make any element draggable.
 */
interface Ext_dd_DragSource extends Ext_dd_DDProxy {


    new(el:string, config?:Object): Ext_dd_DragSource;
    new(el:Html_dom_Element, config?:Object): Ext_dd_DragSource;
    new(el:Ext_dom_Element, config?:Object): Ext_dd_DragSource;


    afterDragDrop(target:Ext_dd_DragDrop, e:Object, id:string): void;

    afterDragEnter(target:Ext_dd_DragDrop, e:Object, id:string): void;

    afterDragOut(target:Ext_dd_DragDrop, e:Object, id:string): void;

    afterDragOver(target:Ext_dd_DragDrop, e:Object, id:string): void;

    afterInvalidDrop(e:Object, id:string): void;

    afterValidDrop(target:Ext_dd_DragDrop, e:Object, id:string): void;

    alignElWithMouse(el:Html_dom_Element, iPageX:number, iPageY:number): void;

    beforeDragDrop(target:Ext_dd_DragDrop, e:Object, id:string) : boolean;

    beforeDragEnter(target:Ext_dd_DragDrop, e:Object, id:string) : boolean;

    beforeDragOut(target:Ext_dd_DragDrop, e:Object, id:string) : boolean;

    beforeDragOver(target:Ext_dd_DragDrop, e:Object, id:string) : boolean;

    beforeInvalidDrop(target:Ext_dd_DragDrop, e:Object, id:string) : boolean;

    getDragData(e:Object): Object;

    getProxy(): Ext_dd_StatusProxy;

    hideProxy(): void;

    onBeforeDrag(data:Object, e:Object): boolean;

    onStartDrag(x:number, y:number): void;

}


/**
 *  This class provides a container DD instance that allows dragging of multiple child source nodes.
 */
interface Ext_dd_DragZone extends Ext_dd_DragSource {

    dragData: Object;

    new(el:string, config:Object): Ext_dd_DragZone;
    new(el:HTMLElement, config:Object): Ext_dd_DragZone;
    new(el:Ext_dom_Element, config:Object): Ext_dd_DragZone;

    afterRepair(): void;

    getDragData(event:Event): Object;

    getRepairXY(event:Event): number[];

    onInitDrag(x:number, y:number): boolean;

}

/**
 *      A specialized floating Component that supports a drop status icon, Ext.Layer styles and auto-repair.
 *      This is the default drag proxy used by all Ext.dd components.
 */
interface Ext_dd_StatusProxy extends Ext_Component {


    new(config?:Object): Ext_dd_StatusProxy;


    getGhost(): Ext_dom_Element;

    //hide(clear:boolean): void;

    repair(xy:number[], callback:Function, scope:Object): void;

    reset(clearGhost:boolean): void;

    setStatus(cssClass:string): void;

    stop(): void;

    sync(): void;

    update(html:string): void;
    update(html:Html_dom_Element): void;

}


/**
 *      A DragDrop implementation that does not move, but can be a drop target.
 */
interface Ext_dd_DDTarget extends Ext_dd_DragDrop {


    new(id:string, sGroup:string, config:Object): Ext_dd_DDTarget;


    addInvalidHandleClass(): void;

    addInvalidHandleId(): void;

    addInvalidHandleType(): void;

    clearConstraints(): void;

    clearTicks(): void;

    endDrag(): void;

    //getDragEl(): void;

    //isValidHandleChild(): void;

    onDrag(): void;

    onDragDrop(): void;

    onDragEnter(): void;

    onDragOut(): void;

    onDragOver(): void;

    onInvalidDrop(): void;

    onMouseDown(): void;

    onMouseUp(): void;

    removeInvalidHandleClass(): void;

    removeInvalidHandleId(): void;

    removeInvalidHandleType(): void;

    resetConstraints(): void;

    setDragElId(): void;

    setHandleElId(): void;

    setInitPosition(): void;

    setOuterHandleElId(): void;

    setXConstraint(): void;

    setYConstraint(): void;

    startDrag(): void;

    toString(): string;
}


/**
 * A simple class that provides the basic implementation needed
 * to make any element a drop target that can have draggable items dropped onto it.
 */
interface Ext_dd_DropTarget extends Ext_dd_DDTarget {

    isTarget: boolean;

    new(el:string, config:Object): Ext_dd_DropTarget;
    new(el:HTMLElement, config:Object): Ext_dd_DropTarget;
    new(el:Ext_dom_Element, config:Object): Ext_dd_DropTarget;

    notifyDrop(source:Ext_dd_DragSource, e:Event, data:Object): boolean;

    notifyEnter(source:Ext_dd_DragSource, e:Event, data:Object): String;

    notifyOut(source:Ext_dd_DragSource, e:Event, data:Object): void;

    notifyOver(source:Ext_dd_DragSource, e:Event, data:Object): String;

}


/**
 * This class provides a container DD instance that allows dropping on multiple child target nodes.
 */
interface Ext_dd_DropZone extends Ext_dd_DropTarget {

    getTargetFromEvent(event:Event): Object;

    notifyDrop(source:Ext_dd_DragSource, e:Event, data:Object): boolean;

    notifyEnter(source:Ext_dd_DragSource, e:Event, data:Object): String;

    notifyOut(source:Ext_dd_DragSource, e:Event, data:Object): void;

    notifyOver(source:Ext_dd_DragSource, e:Event, data:Object): String;

    onContainerDrop(source:Ext_dd_DragSource, e:Event, data:Object): boolean;

    onContainerOver(source:Ext_dd_DragSource, e:Event, data:Object): String;

    onNodeDrop(nodeData:Object, source:Ext_dd_DragSource, e:Event, data:Object): boolean;

    onNodeEnter(nodeData:Object, source:Ext_dd_DragSource, e:Event, data:Object): void;

    onNodeOut(nodeData:Object, source:Ext_dd_DragSource, e:Event, data:Object): void;

    onNodeOver(nodeData:Object, source:Ext_dd_DragSource, e:Event, data:Object): String;

}


/**
 *  Provides automatic scrolling of overflow regions in the page during drag operations.
 */
interface Ext_dd_ScrollManager {

    animDuration: number;

    animate: boolean;

    ddGroup: string;

    frequency: number;

    hthresh: number;

    increment: number;

    vthresh: number;

    new(): Ext_dd_ScrollManager;

    refreshCache(): void;

    register(el:string): void;

    register(el:HTMLElement): void;

    register(el:Ext_dom_Element): void;

    register(el:string[]): void;

    register(el:HTMLElement[]): void;

    register(el:Ext_dom_Element[]): void;

    unregister(el:string): void;

    unregister(el:HTMLElement): void;

    unregister(el:Ext_dom_Element): void;

    unregister(el:string[]): void;

    unregister(el:HTMLElement[]): void;

    unregister(el:Ext_dom_Element[]): void;

}

//TODO: required by the above but are not so important and therefore not defined yet

interface Ext_Version {}

interface Ext_data_reader_Reader {}
interface Ext_data_writer_Writer {}
interface Ext_data_Operation {}
interface Ext_data_Request {}
interface Ext_data_Batch {}
interface Ext_data_Store_PageMap {}

interface Ext_util_KeyMap {}
interface Ext_util_HashMap {}
interface Ext_util_ComponentDragger {}

interface Ext_ElementLoader {}
interface Ext_ComponentLoader {}





