<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Succès</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .success { color: green; font-size: 18px; }
        a { margin-top: 20px; display: inline-block; }
    </style>
</head>
<body>
<h1>Demande Créée</h1>
<p class="success">${message}</p>
<a href="${pageContext.request.contextPath}/demande/">Créer une nouvelle demande</a>
<br>
<a href="${pageContext.request.contextPath}/demande/list">Voir la liste des demandes</a>
</body>
</html>
