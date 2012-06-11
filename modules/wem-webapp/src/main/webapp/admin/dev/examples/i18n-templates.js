var tpl = new Ext.XTemplate(
    '<div style="background-color:LemonChiffon;">',
    '<div>Inside a template</div>',
    '<div style="font-size: 120%">',
    '{[App.lib.util.Messages.get("exampleText", "black", "honey badger", "sleeping", "tiger")]}',
    '</div>',
    '</div>'
);
