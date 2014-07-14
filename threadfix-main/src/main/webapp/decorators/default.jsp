<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common/taglibs.jsp"%>
<html lang="en">
<head>
	<%@ include file="/common/meta.jsp" %>
	<title><decorator:title/> | <spring:message code="webapp.name"/></title>

	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/styles/reset-fonts-grids.css"/>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/styles/bootstrap.min.css"/>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/styles/main.css"/>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/styles/jquery-ui.css"/>

    <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/angular-file-upload-shim.min.js"></script>

    <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/angular.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/ui-bootstrap-tpls-0.10.0.min.js"></script>

    <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/angular-file-upload.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/dynamic-forms.js"></script>

    <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/filters.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/threadfix-module.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/services.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/generic-modal-controller.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/header-controller.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/init-controller.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/directives.js"></script>

    <!--[if lt IE 7]>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/styles/ie6.css"/>
		<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/iepngfix_tilebg.js"></script>
	<![endif]-->
    
	<decorator:head/>
</head>

<body ng-app='threadfix'
      <decorator:getProperty property="body.id" writeEntireProperty="true"/>
      <decorator:getProperty property="body.ng-controller" writeEntireProperty="true"/>
      <decorator:getProperty property="body.ng-init" writeEntireProperty="true"/>
      <decorator:getProperty property="body.ng-class" writeEntireProperty="true"/>
	  <decorator:getProperty property="body.class" writeEntireProperty="true"/>
	  <decorator:getProperty property="body.ng-file-drop" writeEntireProperty="true"/>>

    <spring:url value="" var="emptyUrl" htmlEscape="true"/>
	<div id="wrapper">
        <div id="main">
			<jsp:include page="/common/header.jsp"/>
			<div class="top-corners corners">
				<div class="left corner"><!-- --></div>
				<div class="right corner"><!-- --></div>
				<div class="center"><!-- --></div>
			</div>
			<div id="main-content">
                {{name}}
				<decorator:body/>
			</div>			
			<div class="bottom-corners corners">
				<div class="left corner"><!-- --></div>
				<div class="right corner"><!-- --></div>
				<div class="center"><!-- --></div>
			</div>
		</div>
	</div>
	<jsp:include page="/common/footer.jsp"/>
	<jsp:include page="/common/delete.jsp"/>
</body>
</html>
