<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js" type="text/javascript"></script>
<script src="/ImageHost/static/js/ui.js"></script>
<script src="/ImageHost/static/js/doc.js"></script>
<script src="/ImageHost/static/js/input.js"></script>
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

            <ul class="pure-menu-list">
                <li class="pure-menu-item"><a href="/ImageHost/index" class="pure-menu-link">Images</a></li>
                <li class="pure-menu-item"><a href="/ImageHost/user" class="pure-menu-link">User</a></li>
            </ul>
            <a class="pure-menu-heading" href="/ImageHost/about">About</a>
        </div>
    </div>

    <div class="header">
        <h1>About</h1><br/>
        <h2>To help lost lambs.</h2>

    </div>

    <div class="content">
        <div>
            <h2 class="content-subhead">What are you doing here?</h2>
            <p>
                Trusted timestamping is the process of securely keeping track of the creation and modification time of a document.
                Security here means that no one?not even the owner of the document?should be able to change it once it has been
                recorded provided that the timestamper's integrity is never compromised.
            </p>
            <p>
                <a href="https://www.freetsa.org/index_en.php">FreeTSA</a>.
                <a href="https://en.wikipedia.org/wiki/Trusted_timestamping">Timestamping</a>.
            </p>
        </div>

</body>
</html>


