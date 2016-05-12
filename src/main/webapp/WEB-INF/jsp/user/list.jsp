<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>

<c:url value="/user" var="userUrl" />

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"></meta>
<title>Users - Flay in home</title>
<link rel="stylesheet" href="/webjars/bootstrap/3.3.6/dist/css/bootstrap.min.css"/>
<script src="/webjars/jQuery/2.2.3/dist/jquery.min.js"></script>
<script src="/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"></script>
<script type="text/javascript">
$(document).ready(function() {
    $('h1').animate({
        fontSize: '48px'
    }, "slow");
});
function deleteUser(id) {
	document.getElementById("form-method-" + id).value = "DELETE";
	document.getElementById("form-" + id).submit();
}
</script>
</head>
<body>
<h1>User list</h1>

<table>
	<c:forEach items="${userList}" var="user">
		<form action="${userUrl}" method="post" id="form-${user.id}">
			<input type="hidden" name="_method" value="POST" id="form-method-${user.id}"/>
			<input type="hidden" name="id" value="${user.id}"/> 
			<tr>
			    <td><input name="name" value="${user.name}"/></td>
			    <td><input name="password" value="${user.password}"/></td>
			    <td><input type="submit" value="Edit" /></td>
			    <td><input type="button" onclick="deleteUser(${user.id})" value="Delete" /></td>
			    <td><a href="${userUrl}/${user.id}">Detail</a></td>
			</tr> 
		</form>
	</c:forEach>
	<form action="${userUrl}" method="post">
		<input type="hidden" name="_method" value="POST"/>
		<tr>
		    <td><input name="name" value=""/></td>
		    <td><input name="password" value=""/></td>
		    <td><input type="submit" value="Save" /></td>
		    <td></td>
		</tr> 
	</form>
	
</table>

<form action="/logout" method="post" style="float:right">
	<input type="submit" value="Sign Out"/>
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>
        
</body>
</html>