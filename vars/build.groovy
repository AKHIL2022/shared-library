def call(Map params) {
    validate(params)
    def script = loadScript(params.type)
    script.call(params)
}

private void validate(Map params) {
    if (!params?.type) {
        def available = getAvailableScripts()
        error "Missing 'type' parameter. Available types:\n- ${available.join("\n- ")}"
    }
}

private def loadScript(String type) {
    try {
        // Convert type "build/mend" to "vars/build/mend.groovy"
        def scriptPath = "vars/${type}.groovy".toString()
        
        if (!fileExists(scriptPath)) {
            error "Script '${type}' not found at: ${scriptPath}"
        }
        
        return load(scriptPath)
    } catch(ex) {
        def available = getAvailableScripts()
        error "Failed to load '${type}'. Available types:\n- ${available.join("\n- ")}"
    }
}

private List<String> getAvailableScripts() {
    // Get all .groovy files under vars/**/
    def scripts = []
    def varsDir = new File("${libraryResource('vars')}")
    
    varsDir.eachFileRecurse(groovy.io.FileType.FILES) { file ->
        if (file.name.endsWith('.groovy')) {
            def path = file.path.replace("${varsDir.path}/", "").replace(".groovy", "")
            scripts << path
        }
    }
    
    return scripts
}
