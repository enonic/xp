module api.ui.grid {

    export class GridColumnBuilder<T extends Slick.SlickData> {

        asyncPostRender: (cellNode:any, row:any, dataContext:any, colDef:any) => void;

        behavior: any;

        cannotTriggerInsert: boolean;

        cssClass: string;

        defaultSortAsc: boolean;

        editor: Slick.Editors.Editor<T>;

        field: string;

        focusable: boolean;

        formatter: Slick.Formatter<T>;

        headerCssClass: string;

        id: string;

        maxWidth: number;

        minWidth: number;

        name: string;

        rerenderOnResize: boolean;

        resizable: boolean;

        selectable: boolean;

        sortable: boolean;

        toolTip: string;

        width: number;

        constructor(source?: GridColumn<T>) {

            if (source) {
                this.asyncPostRender = source.getAsyncPostRender();
                this.behavior = source.getBehavior();
                this.cannotTriggerInsert = source.isCannotTriggerInsert();
                this.cssClass = source.getCssClass();
                this.defaultSortAsc = source.isDefaultSortAsc();
                this.editor = source.getEditor();
                this.field = source.getField();
                this.focusable = source.isFocusable();
                this.formatter = source.getFormatter();
                this.headerCssClass = source.getHeaderCssClass();
                this.id = source.getId();
                this.maxWidth = source.getMaxWidth();
                this.minWidth = source.getMinWidth();
                this.name = source.getName();
                this.rerenderOnResize = source.isRerenderOnResize();
                this.resizable = source.isResizable();
                this.selectable = source.isSelectable();
                this.sortable = source.isSortable();
                this.toolTip = source.getToolTip();
                this.width = source.getWidth();
            }
        }

        setAsyncPostRender(asyncPostRender: (cellNode:any, row:any, dataContext:any, colDef:any) => void): GridColumnBuilder<T> {
            this.asyncPostRender = asyncPostRender;
            return this;
        }
        setBehavior(behavior: any): GridColumnBuilder<T> {
            this.behavior = behavior;
            return this;
        }
        setCannotTriggerInsert(cannotTriggerInsert: boolean): GridColumnBuilder<T> {
            this.cannotTriggerInsert = cannotTriggerInsert;
            return this;
        }
        setCssClass(cssClass: string): GridColumnBuilder<T> {
            this.cssClass = cssClass;
            return this;
        }
        setDefaultSortAsc(defaultSortAsc: boolean): GridColumnBuilder<T> {
            this.defaultSortAsc = defaultSortAsc;
            return this;
        }
        setEditor(editor: Slick.Editors.Editor<T>): GridColumnBuilder<T> {
            this.editor = editor;
            return this;
        }
        setField(field: string): GridColumnBuilder<T> {
            this.field = field;
            return this;
        }
        setFocusable(focusable: boolean): GridColumnBuilder<T> {
            this.focusable = focusable;
            return this;
        }
        setFormatter(formatter: Slick.Formatter<T>): GridColumnBuilder<T> {
            this.formatter = formatter;
            return this;
        }
        setHeaderCssClass(headerCssClass: string): GridColumnBuilder<T> {
            this.headerCssClass = headerCssClass;
            return this;
        }
        setId(id: string): GridColumnBuilder<T> {
            this.id = id;
            return this;
        }
        setMaxWidth(maxWidth: number): GridColumnBuilder<T> {
            this.maxWidth = maxWidth;
            return this;
        }
        setMinWidth(minWidth: number): GridColumnBuilder<T> {
            this.minWidth = minWidth;
            return this;
        }
        setName(name: string): GridColumnBuilder<T> {
            this.name = name;
            return this;
        }
        setRerenderOnResize(rerenderOnResize: boolean): GridColumnBuilder<T> {
            this.rerenderOnResize = rerenderOnResize;
            return this;
        }
        setResizable(resizable: boolean): GridColumnBuilder<T> {
            this.resizable = resizable;
            return this;
        }
        setSelectable(selectable: boolean): GridColumnBuilder<T> {
            this.selectable = selectable;
            return this;
        }
        setSortable(sortable: boolean): GridColumnBuilder<T> {
            this.sortable = sortable;
            return this;
        }
        setToolTip(toolTip: string): GridColumnBuilder<T> {
            this.toolTip = toolTip;
            return this;
        }
        setWidth(width: number): GridColumnBuilder<T> {
            this.width = width;
            return this;
        }

        build(): GridColumn<T> {
            return new GridColumn<T>(this);
        }
    }

    export class GridColumn<T extends Slick.SlickData> implements Slick.Column<T> {

        asyncPostRender: (cellNode:any, row:any, dataContext:any, colDef:any) => void;

        behavior: any;

        cannotTriggerInsert: boolean;

        cssClass: string;

        defaultSortAsc: boolean;

        editor: Slick.Editors.Editor<T>;

        field: string;

        focusable: boolean;

        formatter: Slick.Formatter<T>;

        headerCssClass: string;

        id: string;

        maxWidth: number;

        minWidth: number;

        name: string;

        rerenderOnResize: boolean;

        resizable: boolean;

        selectable: boolean;

        sortable: boolean;

        toolTip: string;

        width: number;

        constructor(builder: GridColumnBuilder<T>) {
            this.asyncPostRender = builder.asyncPostRender;
            this.behavior = builder.behavior;
            this.cannotTriggerInsert = builder.cannotTriggerInsert;
            this.cssClass = builder.cssClass;
            this.defaultSortAsc = builder.defaultSortAsc;
            this.editor = builder.editor;
            this.field = builder.field;
            this.focusable = builder.focusable;
            this.formatter = builder.formatter;
            this.headerCssClass = builder.headerCssClass;
            this.id = builder.id;
            this.maxWidth = builder.maxWidth;
            this.minWidth = builder.minWidth;
            this.name = builder.name;
            this.rerenderOnResize = builder.rerenderOnResize;
            this.resizable = builder.resizable;
            this.selectable = builder.selectable;
            this.sortable = builder.sortable;
            this.toolTip = builder.toolTip;
            this.width = builder.width;
        }

        getAsyncPostRender(): (cellNode:any, row:any, dataContext:any, colDef:any) => void {
            return this.asyncPostRender;
        }
        getBehavior(): any {
            return this.behavior;
        }
        isCannotTriggerInsert(): boolean {
            return this.cannotTriggerInsert;
        }
        getCssClass(): string {
            return this.cssClass;
        }
        isDefaultSortAsc(): boolean {
            return this.defaultSortAsc;
        }
        getEditor(): Slick.Editors.Editor<T> {
            return this.editor;
        }
        getField(): string {
            return this.field;
        }
        isFocusable(): boolean {
            return this.focusable;
        }
        getFormatter(): Slick.Formatter<T> {
            return this.formatter;
        }
        getHeaderCssClass(): string {
            return this.headerCssClass;
        }
        getId(): string {
            return this.id;
        }
        getMaxWidth(): number {
            return this.maxWidth;
        }
        getMinWidth(): number {
            return this.minWidth;
        }
        getName(): string {
            return this.name;
        }
        isRerenderOnResize(): boolean {
            return this.rerenderOnResize;
        }
        isResizable(): boolean {
            return this.resizable;
        }
        isSelectable(): boolean {
            return this.selectable;
        }
        isSortable(): boolean {
            return this.sortable;
        }
        getToolTip(): string {
            return this.toolTip;
        }
        getWidth(): number {
            return this.width;
        }

        setAsyncPostRender(asyncPostRender: (cellNode:any, row:any, dataContext:any, colDef:any) => void): GridColumn<T> {
            this.asyncPostRender = asyncPostRender;
            return this;
        }
        setBehavior(behavior: any): GridColumn<T> {
            this.behavior = behavior;
            return this;
        }
        setCannotTriggerInsert(cannotTriggerInsert: boolean): GridColumn<T> {
            this.cannotTriggerInsert = cannotTriggerInsert;
            return this;
        }
        setCssClass(cssClass: string): GridColumn<T> {
            this.cssClass = cssClass;
            return this;
        }
        setDefaultSortAsc(defaultSortAsc: boolean): GridColumn<T> {
            this.defaultSortAsc = defaultSortAsc;
            return this;
        }
        setEditor(editor: Slick.Editors.Editor<T>): GridColumn<T> {
            this.editor = editor;
            return this;
        }
        setField(field: string): GridColumn<T> {
            this.field = field;
            return this;
        }
        setFocusable(focusable: boolean): GridColumn<T> {
            this.focusable = focusable;
            return this;
        }
        setFormatter(formatter: Slick.Formatter<T>): GridColumn<T> {
            this.formatter = formatter;
            return this;
        }
        setHeaderCssClass(headerCssClass: string): GridColumn<T> {
            this.headerCssClass = headerCssClass;
            return this;
        }
        setId(id: string): GridColumn<T> {
            this.id = id;
            return this;
        }
        setMaxWidth(maxWidth: number): GridColumn<T> {
            this.maxWidth = maxWidth;
            return this;
        }
        setMinWidth(minWidth: number): GridColumn<T> {
            this.minWidth = minWidth;
            return this;
        }
        setName(name: string): GridColumn<T> {
            this.name = name;
            return this;
        }
        setRerenderOnResize(rerenderOnResize: boolean): GridColumn<T> {
            this.rerenderOnResize = rerenderOnResize;
            return this;
        }
        setResizable(resizable: boolean): GridColumn<T> {
            this.resizable = resizable;
            return this;
        }
        setSelectable(selectable: boolean): GridColumn<T> {
            this.selectable = selectable;
            return this;
        }
        setSortable(sortable: boolean): GridColumn<T> {
            this.sortable = sortable;
            return this;
        }
        setToolTip(toolTip: string): GridColumn<T> {
            this.toolTip = toolTip;
            return this;
        }
        setWidth(width: number): GridColumn<T> {
            this.width = width;
            return this;
        }
    }
}