<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"></meta>
<title>Login - Flay</title>
</head>
<body>

	<h1>Welcome!</h1>

	<p>Click <a href="/user">here</a> to see users.</p>
	<p>Click <a href="/h2console">here</a> to see H2 console</p>

<form action="/logout" method="post">
	<input type="submit" value="Sign Out"/>
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>

</body>
</html>