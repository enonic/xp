module app.wizard.page.contextwindow.insert {

    export class Insertables {

        public static IMAGE: Insertable = new Insertable().
            setName("image").
            setDisplayName("Image").
            setDescription("Upload or use existing images").
            setIconCls("image");

        public static PART: Insertable = new Insertable().
            setName("part").
            setDisplayName("Part").
            setDescription("Advanced components").
            setIconCls("part");

        public static LAYOUT: Insertable = new Insertable().
            setName("layout").
            setDisplayName("Layout").
            setDescription("Customize page layout").
            setIconCls("layout");

        public static TEXT: Insertable = new Insertable().
            setName("text").
            setDisplayName("Text").
            setDescription("Write directly on the page").
            setIconCls("text");


        public static ALL: Insertable[] = [Insertables.IMAGE, Insertables.PART, Insertables.LAYOUT,
            Insertables.TEXT];

    }
}
