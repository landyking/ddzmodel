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
<body>
hello china!
</body>
</html>