//确定项目根目录
def rootPath = ""
if (binding.variables.containsKey("project")) {
    //如果是使用gmaven-plugin插件执行
    rootPath = project.basedir.getAbsolutePath()
    println "has project binding!"
} else {
    //如果是开发阶段右键执行
    rootPath = new File(getClass().getResource("/").toURI())
            .getParentFile().getParentFile().getParentFile().getParentFile().getAbsolutePath()
    println "no project binding!"
}
println "rootPath:" + rootPath

//确定代码生成器根目录
def genPath = rootPath + "/src/main/codeGenerator"


GenProtocolBeans.generator(genPath,
        new File(rootPath + "/src/main/docs/protocols.xml"),
        rootPath + "/src/main"
)
println "generator Protocol over....."
