<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">

<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js" type="text/javascript"></script>
<script src="/ImageHost/static/js/ui.js"></script>
<script src="/ImageHost/static/js/doc.js"></script>
<script src="/ImageHost/static/js/expandcollapse.js"></script>
<script src="/ImageHost/static/js/jquery.growl.js" type="text/javascript"></script>


<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Image hosting</title>


    <link rel="stylesheet" href="http://yui.yahooapis.com/pure/0.6.0/pure-min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    <link href="<c:url value='/static/css/layouts/table.css' />" rel="stylesheet">
    <link href="<c:url value='/static/css/layouts/marketing-old-ie.css' />" rel="stylesheet">
    <link href="<c:url value='/static/css/layouts/marketing.css' />" rel="stylesheet">
    <link href="<c:url value='/static/css/layouts/side-menu-old-ie.css' />" rel="stylesheet">
    <link href="<c:url value='/static/css/layouts/side-menu.css' />" rel="stylesheet">
    <link href="<c:url value='/static/css/growl.css' />" rel="stylesheet" type="text/css">

</head>

<body>

<div id="layout">
    <!-- Menu toggle -->
    <a href="#menu" id="menuLink" class="menu-link" onclick="expandMenu()">
        <!-- Hamburger icon -->
        <span></span>
    </a>
    <div id="menu">
        <div class="pure-menu">
            <a class="pure-menu-heading" href="/ImageHost/index">Images</a>

            <ul class="pure-menu-list">
                <li class="pure-menu-item"><a href="/ImageHost/user" class="pure-menu-link">User</a></li>
                <li class="pure-menu-item"><a href="/ImageHost/about" class="pure-menu-link">About</a></li>
            </ul>
        </div>
    </div>

    <div class="header">
        <h1>${document.name}</h1><br>

    </div>

    <div class="content">

        <div class="content-head is-center">
            <button class="button-run pure-button" onclick="runScript('${document.id}','${document.name}');"><i
                    class="fa fa-angle-double-right">Verify now</i></button>
            <button class="button-error pure-button" onclick="deleteScript('${document.id}','${document.name}');"><i
                    class="fa fa-times">Delete</i></button>
            <br><br>
            <button class="button-secondary pure-button" onclick="location.href='/ImageHost/${document.id}/tsa';"><i
                    class="fa fa-download">Download image and TSA</i></button>
            <button class="button-secondary pure-button" onclick="location.href='/ImageHost/${document.id}/key';"><i
                    class="fa fa-download">Download encrypted image and key</i></button>

        </div>
    </div>
</div>
<script>
    errorResult('${MESSAGE}');
    errorResult3('${MESSAGE3}');
    successAdd('${MESSAGE2}');
</script>
</body>
</html>


