module app.contextwindow.insert {

    export class Insertables {

        public static IMAGE: Insertable = new Insertable().
            setName("image").
            setDisplayName("Image").
            setDescription("Drag and drop to insert an image component").
            setIconCls("image");

        public static PART: Insertable = new Insertable().
            setName("part").
            setDisplayName("Part").
            setDescription("Drag and drop to insert a part component").
            setIconCls("part");

        public static LAYOUT: Insertable = new Insertable().
            setName("layout").
            setDisplayName("Layout").
            setDescription("Drag and drop to insert a layout component").
            setIconCls("layout");

        public static PARAGRAPH: Insertable = new Insertable().
            setName("paragraph").
            setDisplayName("Paragraph").
            setDescription("Drag and drop to insert a paragraph component").
            setIconCls("paragraph");


        public static ALL: Insertable[] = [Insertables.IMAGE, Insertables.PART, Insertables.LAYOUT,
            Insertables.PARAGRAPH];

    }
}
