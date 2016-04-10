<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"></meta>
<title>Users - JPA Demo</title>
</head>
<body>
<h1>User list by jsp</h1>

<table>
	<c:forEach items="${userList}" var="user">
	<tr>
	    <td>${user.name}</td>	
	</tr> 
	</c:forEach>
</table>

</body>
</html>