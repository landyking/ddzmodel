<%@ page import="java.io.File" %>
<%--
  Created by IntelliJ IDEA.
  User: landy
  Date: 15/3/27
  Time: 下午2:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <meta charset="utf-8">
    <link rel="icon" type="image/GIF" href="js/poker/res/favicon.ico"/>
    <meta name="apple-mobile-web-app-capable" content="yes"/>
    <meta name="full-screen" content="yes"/>
    <meta name="screen-orientation" content="portrait"/>
    <meta name="x5-fullscreen" content="true"/>
    <meta name="360-fullscreen" content="true"/>
    <style>
        body, canvas, div {
            -moz-user-select: none;
            -webkit-user-select: none;
            -ms-user-select: none;
            -khtml-user-select: none;
            -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
        }
    </style>
    <script src="js/jquery-2.1.3.min.js"></script>
    <script src="js/sockjs.min.js"></script>
    <script src="js/ws.js"></script>
    <script src="js/ServerRequest.js"></script>
    <script src="js/ServerResponse.js"></script>
    <%
        String realPath = request.getServletContext().getRealPath("/js/response");
        File dir = new File(realPath);
        for (String f : dir.list()) {
    %>
    <script src="js/response/<%=f%>"></script>
    <%
        }
    %>
</head>
<body style="padding:0; margin: 0; background: #000;">
<canvas id="gameCanvas" width="480" height="320"></canvas>
<script src="js/frameworks/cocos2d-html5/CCBoot.js"></script>
<script src="js/poker/main.js"></script>
</body>
</html>
