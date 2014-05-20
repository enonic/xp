module LiveEdit.component {

    import ItemType = api.liveedit.ItemType;
    import PageComponentSelectEvent = api.liveedit.PageComponentSelectEvent;

    export class ComponentPlaceholder extends Component {
        constructor(type: ItemType, className: string = 'live-edit-empty-component') {
            super(type);
            this.addClass(className);
            this.getEl().setData('live-edit-empty-component', 'true');

            PageComponentSelectEvent.on(() => this.onSelect());

            $liveEdit(window).on('componentDeselect.liveEdit', (event, name?)=> {
                this.onDeselect();
            });
        }

        static fromComponent(type: LiveEdit.component.Type): ComponentPlaceholder {
            var placeholder: ComponentPlaceholder;
            if (type === Type.IMAGE) {
                placeholder = new LiveEdit.component.ImagePlaceholder();
            } else if (type == Type.PART) {
                placeholder = new LiveEdit.component.PartPlaceholder();
            } else if (type == Type.LAYOUT) {
                placeholder = new LiveEdit.component.LayoutPlaceholder();
            } else if (type == Type.TEXT) {
                placeholder = new LiveEdit.component.TextPlaceholder();
            } else {
                var emptyComponentIcon = new api.dom.DivEl();
                emptyComponentIcon.addClass('live-edit-empty-component-icon');
                var typeAsString: string = LiveEdit.component.Type[type];
                typeAsString = typeAsString.toLowerCase();
                placeholder = new ComponentPlaceholder(ItemType.byShortName(typeAsString));
                placeholder.appendChild(emptyComponentIcon);
            }
            return placeholder;
        }

        onSelect() {

        }

        onDeselect() {

        }

    }
}