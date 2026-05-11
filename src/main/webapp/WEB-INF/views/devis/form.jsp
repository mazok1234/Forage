<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Creer un devis</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .actions { margin-bottom: 15px; }
        .error { color: red; }
        .form-group { margin-top: 12px; }
        button { margin-right: 8px; }
    </style>
</head>
<body>
<h1>Creer un devis</h1>

<div class="actions">
    <a href="${pageContext.request.contextPath}/devis/demandes">Retour aux demandes approuvees</a>
    <a href="${pageContext.request.contextPath}/devis/list">Voir les devis</a>
</div>

<c:if test="${not empty error}"><p class="error">${error}</p></c:if>

<div>
    <strong>Demande:</strong>
    ID ${demande.id} - ${demande.client.nom}, ${demande.commune.libelle}, ${demande.lieu}
</div>

<form method="post" action="${pageContext.request.contextPath}/devis/create">
    <input type="hidden" name="demandeId" value="${demande.id}">

    <div class="form-group">
        <label for="libelle">Libelle :</label>
        <input type="text" id="libelle" name="libelle" required>
    </div>
    <div class="form-group">
        <label for="montant">Montant :</label>
        <input type="number" id="montant" name="montant" min="0" step="0.01" required>
    </div>

    <div class="form-group">
        <button type="submit">Enregistrer devis</button>
    </div>
</form>
</body>
</html>
