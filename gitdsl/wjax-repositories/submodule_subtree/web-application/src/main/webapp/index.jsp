<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="gitService" class="de.gitworkshop.web.GitService" />
<html>
<head>
	<link href="./bootstrap/dist/css/bootstrap.css" rel="stylesheet">
	<!-- Bootstrap theme -->
	<link href="./bootstrap/dist/css/bootstrap-theme.css" rel="stylesheet">
</head>

<body>
	<div class="container">

		<div class="page-header">
			<h1>Git Commands</h1>
		</div>
		
		<p>The most commonly used git commands are:</p>
		<table class="table table-striped">
			<thead>
				<tr>
					<th>Description</th>
					<th>Name</th>
				</tr>
			</thead>
			<tbody>
				<c:set var="commands" value="${gitService.gitCommands}"
					scope="request" />
				<c:forEach var="command" items="${commands}">
					<tr>
						<td>${command.description}</td>
						<td>${command.name}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	
	<div class="well">
		<jsp:useBean class="de.gitworkshop.web.WebAppEnvInfo" id="webAppEnvInfoBean">
			WebApp Version: <b>${webAppEnvInfoBean.webAppVersion}</b>, Launched by: <b>${webAppEnvInfoBean.launcher}</b>
		</jsp:useBean>
	</div>
	
</html>
