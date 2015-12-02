<%--
  Created by IntelliJ IDEA.
  User: weapon
  Date: 2015-11-29
  Time: 15:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title></title>
</head>
<body>
<h1>findUser</h1>
<table>
  <c:forEach items="${users}" var="u">
    <tr>
      <td>${u.id}</td>
      <td>${u.username}</td>
      <td>${u.birthday}</td>
    </tr>
  </c:forEach>

</table>
</body>
</html>
