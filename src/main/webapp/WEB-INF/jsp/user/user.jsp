<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix='form'   uri='http://www.springframework.org/tags/form'%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"></meta>
<title>Users - Flay Demo</title>
<link rel="stylesheet" href="/webjars/bootstrap/3.3.6/dist/css/bootstrap.min.css"/>
<script src="/webjars/jQuery/2.2.3/dist/jquery.min.js"></script>
<script src="/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"></script>
</head>
<body>
<h1>User details</h1>

<form:form modelAttribute="user">
<table>
	<tr>
	    <td>
			<form:label path="name">사용자이름:</form:label><br/>
			<form:input path="name" size="12" maxlength="12" />
			<form:errors cssClass="error" path="name" />
	    </td>
	</tr> 
	<tr>
		<td>
			<form:label path="password">비밀번호:</form:label><br/>
			<form:password path="password" showPassword="true" size="12" maxlength="12" />
			<form:errors cssClass="error" path="password" />		
		</td>
	</tr>
	<tr>
		<td><input type="submit" value="작성" /></td>
	</tr>
</table>
</form:form>



<form action="/logout" method="post">
	<input type="submit" value="Sign Out"/>
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>
        
</body>
</html>