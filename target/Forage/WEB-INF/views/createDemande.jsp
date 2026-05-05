<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Créer Demande</title>
</head>
<body>
<h2>Nouvelle Demande</h2>
<form action="${pageContext.request.contextPath}/demande/save" method="post">
    District: <input type="text" name="district"/><br/>
    Commune: <input type="text" name="commune"/><br/>
    Fokontany: <input type="text" name="fokontany"/><br/>
    Date (yyyy-mm-dd): <input type="date" name="dateDemande"/><br/>
    Personne qui demande: <input type="text" name="personneQuiDemande"/><br/>
    <input type="submit" value="Créer"/>
</form>
</body>
</html>
