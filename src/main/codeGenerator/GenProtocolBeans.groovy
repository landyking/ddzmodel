import groovy.text.SimpleTemplateEngine

/**
 * User: landy
 * Date: 15/3/5
 * Time: 下午3:34
 *
 */
class GenProtocolBeans {

    def typeMapping = [s: "String", l: "long", i: "int", S: "short", b: "byte", "[b": "byte[]"]

    def static generator(genPath, File xml, String mainDir) {
        new GenProtocolBeans().gen(genPath, xml, mainDir)
    }

    def gen(genPath, File xml, String mainDir) {
        println "input :" + xml.getAbsolutePath()

        def protocols = []

        def cfg = new XmlParser().parseText(xml.getText("utf8"))


        def cmdSet = []

        cfg.protocol.each { cmd ->

            def protocol = [:]

            protocol.id = cmd["@id"]
            protocol.protocolName = cmd["@name"]
            if (cmdSet.contains(protocol.protocolName)) {
                throw new RuntimeException("Class 重复!" + protocol.protocolName)
            } else {
                cmdSet << protocol.protocolName
            }
            protocol.desc = cmd["@desc"]

            def fieldExtract = { f ->
                def name = f["@name"]
                def setName = "set" + firstUpper(name)
                def getName = "get" + firstUpper(name)
                def type = toJavaType(f["@type"], cmdSet)
                def desc = f["@desc"]

                return [name: name, setName: setName, getName: getName, type: type, desc: desc]
            }
            def arrayExtract = { f ->
                def name = f["@name"]
                def setName = "set" + firstUpper(name)
                def getName = "get" + firstUpper(name)
                def type = toJavaType(f["@type"], cmdSet) + "[]"
                def desc = f["@desc"]

                return [name: name, setName: setName, getName: getName, type: type, desc: desc]
            }

            protocol.req = []
            cmd.req.field.each {
                protocol.req << fieldExtract(it)
            }
            cmd.req.array.field.each {
                protocol.req << arrayExtract(it)
            }
            protocol.resp = []
            cmd.resp.field.each {
                protocol.resp << fieldExtract(it)
            }
            cmd.resp.array.field.each {
                protocol.resp << arrayExtract(it)
            }

            protocols << protocol
        }

        def engine = new SimpleTemplateEngine()

        genServer(engine, genPath, mainDir, protocols)
        genClient(engine, genPath, mainDir, protocols)
    }

    def genClient(SimpleTemplateEngine engine, genPath, String mainDir, ArrayList protocols) {
        def respTpl = engine.createTemplate(new File(genPath + "/tpl/client/response.template").getText("utf8"))
        def serverReqTpl = engine.createTemplate(new File(genPath + "/tpl/client/ServerRequest.template").getText("utf8"))
        def serverRespTpl = engine.createTemplate(new File(genPath + "/tpl/client/ServerResponse.template").getText("utf8"))

        def destDirPath = mainDir + "/webapp/js"
        println "client dest dir path:" + destDirPath

        new File(destDirPath + "/response").mkdirs()

        protocols.each { one ->
            def respJsFile = new File(destDirPath + "/response/" + one.protocolName + "Response.js");
            if (!respJsFile.exists()) {
                println "gen response file:" + respJsFile.getAbsolutePath()
                def respContent = respTpl.make(one).toString()
                //println respContent
                respJsFile.write(respContent, "utf8")
            }
        }
        println "generator response file over......"

        def serverReqJsFile = new File(destDirPath + "/ServerRequest.js");
        println "gen ServerRequest file:" + serverReqJsFile.getAbsolutePath()
        def serverReqContent = serverReqTpl.make([protocols:protocols]).toString()
        //println serverReqContent
        serverReqJsFile.write(serverReqContent, "utf8")
        println "generator ServerRequest.js file over......"

        def serverRespJsFile = new File(destDirPath + "/ServerResponse.js");
        println "gen ServerResponse file:" + serverRespJsFile.getAbsolutePath()
        def serverRespContent = serverRespTpl.make([protocols:protocols]).toString()
        //println serverReqContent
        serverRespJsFile.write(serverRespContent, "utf8")
        println "generator ServerResponse.js file over......"
    }

    def genServer(SimpleTemplateEngine engine, genPath, String mainDir, ArrayList protocols) {
        def handlerTpl = engine.createTemplate(new File(genPath + "/tpl/server/handler.template").getText("utf8"))
        def reqTpl = engine.createTemplate(new File(genPath + "/tpl/server/req.template").getText("utf8"))
        def respTpl = engine.createTemplate(new File(genPath + "/tpl/server/resp.template").getText("utf8"))
        def utilsTpl = engine.createTemplate(new File(genPath + "/tpl/server/ProtocolUtils.template").getText("utf8"))

        def rootPackage = "gen"
        def destDirPath = mainDir + "/java/" + rootPackage.replaceAll("\\.", "/")
        println "server dest dir path:" + destDirPath

        new File(destDirPath + "/handler").mkdirs()
        new File(destDirPath + "/request").mkdirs()
        new File(destDirPath + "/response").mkdirs()

        protocols.each { one ->
            if(one.req.isEmpty())return;
            def handlerJavaFile = new File(destDirPath + "/handler/" + one.protocolName + "Handler.java");
            if (!handlerJavaFile.exists()) {
                println "gen handler file:" + handlerJavaFile.getAbsolutePath()
                def handlerContent = handlerTpl.make(one).toString()
                //println handlerContent
                handlerJavaFile.write(handlerContent, "utf8")
            }
        }
        println "generator handler file over......"

        protocols.each { one ->
            if(one.req.isEmpty())return;
            def reqJavaFile = new File(destDirPath + "/request/" + one.protocolName + "Req.java");
            println "gen request file:" + reqJavaFile.getAbsolutePath()
            def reqContent = reqTpl.make(one).toString()
            //println reqContent
            reqJavaFile.write(reqContent, "utf8")
        }
        println "generator request file over......"

        protocols.each { one ->
            if(one.resp.isEmpty())return;
            def respJavaFile = new File(destDirPath + "/response/" + one.protocolName + "Resp.java");
            println "gen response file:" + respJavaFile.getAbsolutePath()
            def respContent = respTpl.make(one).toString()
            //println respContent
            respJavaFile.write(respContent, "utf8")
        }
        println "generator response file over......"

        def utilsJavaFile = new File(destDirPath + "/ProtocolUtils.java")
        println "gen utils file:" + utilsJavaFile.getAbsolutePath()
        def utilsContent = utilsTpl.make([protocols: protocols]).toString()
        //println utilsContent
        utilsJavaFile.write(utilsContent, "utf8")
        println "generator utils file over......"
    }

    def firstUpper(name) {
        def first = name[0] + ""
        return name.replaceFirst(first, first.toUpperCase())
    }

    def toJavaType(type, cmdSet) {

        if (this.typeMapping.containsKey(type)) {
            return typeMapping[type]
        } else if (cmdSet.contains(type)) {
            return "Unknow"
        }
        return type
    }

    def static main(argus) {
        generator("/Volumes/apple/sources/self/DouDiZhuModel/websocket/src/main/codeGenerator",
                new File("/Volumes/apple/sources/self/DouDiZhuModel/websocket/src/main/docs/protocols.xml"),
                "/Volumes/apple/sources/self/DouDiZhuModel/websocket/src/main")
    }
}