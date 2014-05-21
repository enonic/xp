module LiveEdit.component {

    import ItemType = api.liveedit.ItemType;
    import ImageItemType = api.liveedit.image.ImageItemType;
    import PartItemType = api.liveedit.part.PartItemType;
    import LayoutItemType = api.liveedit.layout.LayoutItemType;
    import TextItemType = api.liveedit.text.TextItemType;
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

        static fromComponent(type: ItemType): ComponentPlaceholder {

            var placeholder: ComponentPlaceholder;

            if (type.equals(ImageItemType.get())) {
                placeholder = new LiveEdit.component.ImagePlaceholder();
            }
            else if (type.equals(PartItemType.get())) {
                placeholder = new LiveEdit.component.PartPlaceholder();
            }
            else if (type.equals(LayoutItemType.get())) {
                placeholder = new LiveEdit.component.LayoutPlaceholder();
            }
            else if (type.equals(TextItemType.get())) {
                placeholder = new LiveEdit.component.TextPlaceholder();
            }
            else {
                var emptyComponentIcon = new api.dom.DivEl();
                emptyComponentIcon.addClass('live-edit-empty-component-icon');
                placeholder = new ComponentPlaceholder(type);
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