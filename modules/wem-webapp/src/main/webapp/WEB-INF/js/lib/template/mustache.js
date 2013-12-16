function openFile(name) {
    return __wem.loadFromModule(name);
}

var factory = new com.github.mustachejava.DefaultMustacheFactory();

function render(name, model) {
    var text = openFile(name).asString();
    return renderText(text, name, model);
}

function renderText(text, name, model) {
    var reader = new java.io.StringReader(text);
    var compiled = factory.compile(reader, name);

    var stringWriter = new java.io.StringWriter();
    compiled.execute(stringWriter, model);

    return stringWriter.toString();
}

exports.render = render;
exports.renderText = renderText;

