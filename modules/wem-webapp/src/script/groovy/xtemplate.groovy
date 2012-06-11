// XTemplate code generation script

log.info "-" * 72
log.info "Generating XTemplate Javascript resources"
log.info "-" * 72
log.info("")

baseDir = new File("${project.basedir}")
templatesRootDir = new File(baseDir, "/src/script/xtemplates/")
outputJsFile = new File(baseDir, "/src/main/webapp/admin/resources/app/view/XTemplates.js")

init()

def init()
{
    def sb = new StringBuilder()
    sb.append(createTemplatesHeader())
    sb.append("\n")

    templatesRootDir.eachFile() { file ->
        if (file.isDirectory() && includeDir(file) ) {
            log.info "Generating XTemplates for namespace '" + file.getName() + "'"
            sb.append(createTemplatesFromDir(file))
            sb.append("\n\n")
        }
    }

    log.info("")
    def fileUpdated = updateFile(sb.toString())
    if (fileUpdated) {
        log.info "Updated the JavaScript XTemplate file: " + outputJsFile.getAbsolutePath()
    } else {
        log.info "No changes found in templates."
    }
}

def createTemplatesHeader()
{
    engine = new groovy.text.GStringTemplateEngine()
    template = engine.createTemplate(new File(baseDir, "/src/script/groovy/TemplateHeader.js.gsp")).make()

    return template.toString()
}

def createTemplatesFromDir(File dir)
{
    def templateList = []

    dir.eachFile() { file ->
        def template = readTemplateFile(file)
        templateList.add(template)
    }
    def namespace = dir.getName()
    def binding = ["templateNamespace": namespace, "templateList": templateList]
    def outputTemplateFile = new File(baseDir, "/src/script/groovy/Templates.js.gsp")

    engine = new groovy.text.GStringTemplateEngine()
    template = engine.createTemplate(outputTemplateFile).make(binding)
    outputXtemplates = template.toString()

    return outputXtemplates
}

def readTemplateFile(File file)
{
    def fileLines = []
    file.eachLine { line -> fileLines.add(line) }
    new TemplateContent(name: file.getName() - ".html", lines: fileLines)
}

def updateFile(String content)
{
    if (outputJsFile.exists() && contentsAreEqual(content, outputJsFile.text)) {
        return false
    }

    outputJsFile.write( content )
    return true
}

def contentsAreEqual(String source, String target) {
    def sourceLines = source.readLines()
    def targetLines = target.readLines()
    if (sourceLines.size != targetLines.size) {
        return false
    }
    // skip comparing first line due to timestamp
    sourceLines.remove(0)
    targetLines.remove(0)

    return sourceLines.join("\r\n") == targetLines.join("\r\n")
}

def includeDir(File dir)
{
    return !dir.getName().startsWith("_")
}