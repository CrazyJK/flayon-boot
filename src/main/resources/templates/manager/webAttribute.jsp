<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<!DOCTYPE html>
<html>
<head>
<title>Web Attribute</title>
</head>
<body>
	<header class="page-header">
		<h1>Web Attribute List</h1>
	</header>

	<article>
		<section id="session-attribute" class="panel">
			<header class="panel-heading">
				<h3 class="panel-title">Session attributes</h3>
			</header>
			<div class="panel-body">
				<ol class="code-view">
					<%
						@SuppressWarnings("rawtypes")
						Enumeration names = session.getAttributeNames();
						while (names.hasMoreElements()) {
							String name = (String) names.nextElement();
							Object value = session.getAttribute(name);
							String clazz = value.getClass().getName();
					%>
					<li>
						<dl>
							<dt class="code-name"><%=name%></dt>
							<dd class="code-value"><%=value%></dd>
							<dd class="code-clazz"><%=clazz%></dd>
						</dl>
					</li>
					<%
						}
					%>
				</ol>
			</div>
		</section>
		<section id="request-attribute" class="panel">
			<header class="panel-heading">
				<h3 class="panel-title">Request attributes</h3>
			</header>
			<div class="panel-body">
				<ol class="code-view">
					<%
						names = request.getAttributeNames();
						while (names.hasMoreElements()) {
							String name = (String) names.nextElement();
							Object value = request.getAttribute(name);
							String clazz = value.getClass().getName();
					%>
					<li>
						<dl>
							<dt class="code-name"><%=name%></dt>
							<dd class="code-value"><%=value%></dd>
							<dd class="code-clazz"><%=clazz%></dd>
						</dl>
					</li>
					<%
						}
					%>
				</ol>
			</div>
		</section>

	</article>

</body>
</html>